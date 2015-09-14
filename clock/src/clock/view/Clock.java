package clock.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import model.dataset.TimelineDataSet;
import model.event.TimelineChartData;
import model.event.TimelineEvent;
import clock.color.HeatMapColorProvider;
import clock.grouper.Grouper;
import clock.grouper.QuantityLeveler;
import clock.grouper.QuantityLeveler.QuantityLevel;
import clock.grouper.QuantityLeveler.QuantityLevelProvider;
import clock.legend.HorizontalLegend;
import clock.legend.Legend.LegendEntry;
import clock.legend.VerticalLegend;
import clock.model.DayOfWeek;
import clock.view.chart.ClockChart;
import clock.view.chart.ClockChart.ClockChartSelectionEvent;
import clock.view.chart.ClockChart.ClockChartSliceDescriptor;
import clock.view.chart.ClockChart.IClockChartListener;
import clock.view.size.SizeManagingPane;
import clock.view.size.layout.HorizontalLayout;
import clock.view.size.layout.ISizeManagedLayout;
import clock.view.size.layout.VerticalLayout;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class Clock extends Pane {
	private static final double CHART_HEAT_MINIMUM_VALUE = 0.0;

	private static final double CHART_HEAT_MAXIMUM_VALUE = 1.0;

	private final List<IClockSelectionListener> selectionListeners = new ArrayList<>();

	private Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData;

	private Clock(List<TimelineDataSet> timelineDataSets) {

		// clockChart

		groupedData = Grouper.group(timelineDataSets);

		Set<Integer> allEventsCounts = Sets.newHashSet();

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				allEventsCounts.add(groupedData.get(dayOfWeek).get(hour).getEventsCount());
			}
		}

		int minimumEventsCount = Ordering.natural().min(allEventsCounts);
		int maximumEventsCount = Ordering.natural().max(allEventsCounts);

		QuantityLevelProvider quantityLevelProvider = QuantityLeveler.getQuantityLevelProvider(
				CHART_HEAT_MINIMUM_VALUE, CHART_HEAT_MAXIMUM_VALUE, minimumEventsCount, maximumEventsCount);

		final ClockChart clockChart = new ClockChart(groupedData, quantityLevelProvider);

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

		final HorizontalLegend horizontalLegend = new HorizontalLegend(legendEntries);
		final VerticalLegend verticalLegend = new VerticalLegend(legendEntries);

		// top pane

		ISizeManagedLayout horizontalLayout = new HorizontalLayout(clockChart, verticalLegend);
		ISizeManagedLayout verticalLayout = new VerticalLayout(clockChart, horizontalLegend);
		final SizeManagingPane sizeManagingPane = new SizeManagingPane(verticalLayout, horizontalLayout);

		getChildren().add(sizeManagingPane);

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				sizeManagingPane.setMinWidth(value);
				sizeManagingPane.setMaxWidth(value);
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				sizeManagingPane.setMinHeight(value);
				sizeManagingPane.setMaxHeight(value);
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
