package clock.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.dataset.TimelineDataSet;
import model.event.TimelineChartData;
import model.event.TimelineEvent;
import clock.color.HeatMapColorProvider;
import clock.grouper.Grouper;
import clock.grouper.QuantityLeveler;
import clock.grouper.QuantityLeveler.QuantityLevel;
import clock.grouper.QuantityLeveler.QuantityLevelProvider;
import clock.legend.Legend;
import clock.legend.Legend.LegendEntry;
import clock.legend.VerticalLegend;
import clock.model.DayOfWeek;
import clock.view.chart.ClockChart;
import clock.view.chart.ClockChart.ClockChartSelectionEvent;
import clock.view.chart.ClockChart.ClockChartSliceDescriptor;
import clock.view.chart.ClockChart.IClockChartListener;
import clock.view.font.FontSizeManager;

/*
 * TODO
 * 
 * - add vertical legend with switch
 * - enhance all sizes (bind radius and font size)
 * 
 */

public class Clock extends Pane {
	private static final double CHART_HEAT_MINIMUM_VALUE = 0.0;

	private static final double CHART_HEAT_MAXIMUM_VALUE = 1.0;

	private static final double HORIZONTAL_LEGEND_PART = 1.0 / 8.0;

	private static final double VERTICAL_LEGEND_PART = 1.0 / 7.0;

	private final List<IClockSelectionListener> selectionListeners = new ArrayList<>();

	private Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData;

	private Clock(List<TimelineDataSet> timelineDataSets) {

		final HBox box = new HBox();

		// fontSizeManager

		final FontSizeManager fontSizeManager = new FontSizeManager();

		// clockChart

		groupedData = Grouper.group(timelineDataSets);

		int minimumEventsCount = Integer.MAX_VALUE;
		int maximumEventsCount = 0;

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				int eventsCount = groupedData.get(dayOfWeek).get(hour).getEventsCount();
				if (eventsCount < minimumEventsCount) {
					minimumEventsCount = eventsCount;
				}
				if (eventsCount > maximumEventsCount) {
					maximumEventsCount = eventsCount;
				}
			}
		}

		QuantityLevelProvider quantityLevelProvider = QuantityLeveler.getQuantityLevelProvider(
				CHART_HEAT_MINIMUM_VALUE, CHART_HEAT_MAXIMUM_VALUE, minimumEventsCount, maximumEventsCount);

		final ClockChart clockChart = new ClockChart(groupedData, quantityLevelProvider);

		box.getChildren().add(clockChart);

		clockChart.addClockChartListener(new IClockChartListener() {
			@Override
			public void selectionChanged(ClockChartSelectionEvent chartEvent) {
				ClockSelectionEvent event = new ClockSelectionEvent(chartEvent);

				for (IClockSelectionListener listener : selectionListeners) {
					listener.selectionChanged(event);
				}
			}
		});

		// legend

		TreeSet<QuantityLevel> allQuantityLevels = new TreeSet<>(new Comparator<QuantityLevel>() {
			@Override
			public int compare(QuantityLevel level1, QuantityLevel level2) {
				return Double.compare(level2.getLevelValue(), level1.getLevelValue());
			}
		});

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				allQuantityLevels.add(quantityLevelProvider.getLevelForQuantity(groupedData.get(dayOfWeek).get(hour)
						.getEventsCount()));
			}
		}

		List<LegendEntry> legendEntries = new ArrayList<>();

		for (QuantityLevel quantityLevel : allQuantityLevels) {
			legendEntries.add(new LegendEntry(quantityLevel.getLevelDescription(), HeatMapColorProvider
					.getColorForValue(quantityLevel.getLevelValue())));
		}

		final Legend legend = new VerticalLegend(legendEntries);

		box.getChildren().add(legend);

		// management

		getChildren().add(box);

		box.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();

				double newClockChartWidth = value * (1 - VERTICAL_LEGEND_PART);
				double newLegendWidth = value * VERTICAL_LEGEND_PART;
				// double newClockChartWidth = value;
				// double newLegendWidth = value;

				clockChart.setWidth(newClockChartWidth);
				legend.setWidth(newLegendWidth);
				fontSizeManager.nodeResized(clockChart, newClockChartWidth, clockChart.getHeight());
				fontSizeManager.nodeResized(legend, newLegendWidth, legend.getHeight());
			}
		});

		box.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();

				double newClockChartHeight = value;
				double newLegendHeight = value;
				// double newClockChartHeight = value * (1 -
				// HORIZONTAL_LEGEND_PART);
				// double newLegendHeight = value * HORIZONTAL_LEGEND_PART;

				clockChart.setHeight(newClockChartHeight);
				legend.setHeight(newLegendHeight);
				fontSizeManager.nodeResized(clockChart, clockChart.getWidth(), newClockChartHeight);
				fontSizeManager.nodeResized(legend, legend.getWidth(), newLegendHeight);
			}
		});

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				box.setMinWidth(value);
				box.setMaxWidth(value);
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				box.setMinHeight(value);
				box.setMaxHeight(value);
			}
		});
	}

	// newInstance

	public static Clock newInstance(List<TimelineDataSet> timelineDataSets) {
		return new Clock(timelineDataSets);
	}

	// listeners

	public void addSelectionListener(IClockSelectionListener listener) {
		selectionListeners.add(listener);
	}

	public void removeSelectionListener(IClockSelectionListener listener) {
		selectionListeners.remove(listener);
	}

	public static interface IClockSelectionListener {
		public void selectionChanged(ClockSelectionEvent event);
	}

	public class ClockSelectionEvent {
		private List<ClockChartSliceDescriptor> selectedSlices;

		private ClockSelectionEvent(ClockChartSelectionEvent clockChartSelectionEvent) {
			selectedSlices = clockChartSelectionEvent.getSelectedSlices();
		}

		public Iterable<TimelineEvent> getSelectedEvents() {
			return new ClockEventsIterable(selectedSlices);
		}

		public boolean isSliceSelected(DayOfWeek dayOfWeek, int hour) {
			for (ClockChartSliceDescriptor sliceDescriptor : selectedSlices) {
				if (dayOfWeek == sliceDescriptor.getDayOfWeek() && hour == sliceDescriptor.getHour()) {
					return true;
				}
			}
			return false;
		}

		public int getSelectedEventsCount() {
			int selectedEventsCount = 0;

			for (ClockChartSliceDescriptor sliceDescriptor : selectedSlices) {
				selectedEventsCount += groupedData.get(sliceDescriptor.getDayOfWeek()).get(sliceDescriptor.getHour())
						.getEventsCount();
			}

			return selectedEventsCount;
		}
	}

	private class ClockEventsIterable implements Iterable<TimelineEvent> {
		private List<ClockChartSliceDescriptor> selectedSlices;

		private ClockEventsIterable(List<ClockChartSliceDescriptor> selectedSlices) {
			this.selectedSlices = selectedSlices;
		}

		@Override
		public Iterator<TimelineEvent> iterator() {
			return new Iterator<TimelineEvent>() {
				private final Iterator<ClockChartSliceDescriptor> outerIterator = selectedSlices.iterator();

				private Iterator<TimelineEvent> innerIterator = Collections.emptyIterator();

				@Override
				public void remove() {
				}

				@Override
				public TimelineEvent next() {
					swapIterators();

					return innerIterator.next();
				}

				@Override
				public boolean hasNext() {
					swapIterators();

					return innerIterator.hasNext();
				}

				private void swapIterators() {
					while (!innerIterator.hasNext() && outerIterator.hasNext()) {
						ClockChartSliceDescriptor nextSliceDescriptor = outerIterator.next();

						innerIterator = groupedData.get(nextSliceDescriptor.getDayOfWeek())
								.get(nextSliceDescriptor.getHour()).getEventsList().iterator();
					}
				}
			};
		}

	}
}
