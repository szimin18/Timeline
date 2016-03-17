package clock.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import clock.color.HeatMapColorProvider;
import clock.grouper.Grouper;
import clock.grouper.QuantityLeveler;
import clock.grouper.QuantityLeveler.QuantityLevel;
import clock.grouper.QuantityLeveler.QuantityLevelProvider;
import clock.legend.HorizontalLegend;
import clock.legend.Legend.LegendEntry;
import clock.legend.VerticalLegend;
import clock.model.ClockChartData;
import clock.model.ClockDataSet;
import clock.model.SliceDescriptor;
import clock.view.chart.ClockChart;
import clock.view.chart.ClockChart.ClockChartSelectionEvent;
import clock.view.chart.ClockChart.IClockChartListener;
import clock.view.event.ClockSelectionEvent;
import clock.view.size.SizeManagingPane;
import clock.view.size.layout.HorizontalLayout;
import clock.view.size.layout.ISizeManagedLayout;
import clock.view.size.layout.VerticalLayout;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public final class Clock extends Pane {
	private static final double CHART_HEAT_MINIMUM_VALUE = 0.0;

	private static final double CHART_HEAT_MAXIMUM_VALUE = 1.0;

	private final List<IClockSelectionListener> selectionListeners = new ArrayList<>();

	private Map<SliceDescriptor, ClockChartData> groupedData;

	private Clock(List<ClockDataSet> clockDataSets) {

		// clockChart

		groupedData = Grouper.group(clockDataSets);

		Set<Integer> allEventsCounts = Sets.newHashSet();

		for (ClockChartData clockChartData : groupedData.values()) {
			allEventsCounts.add(clockChartData.getEventsCount());
		}

		int minimumEventsCount = Ordering.natural().min(allEventsCounts);
		int maximumEventsCount = Ordering.natural().max(allEventsCounts);

		QuantityLevelProvider quantityLevelProvider = QuantityLeveler.getQuantityLevelProvider(CHART_HEAT_MINIMUM_VALUE,
				CHART_HEAT_MAXIMUM_VALUE, minimumEventsCount, maximumEventsCount);

		final ClockChart clockChart = new ClockChart(groupedData, quantityLevelProvider);

		clockChart.addClockChartListener(new IClockChartListener() {
			@Override
			public void selectionChanged(ClockChartSelectionEvent chartEvent) {
				ClockSelectionEvent event = new ClockSelectionEvent(groupedData, chartEvent.getSelectedSlices());

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

		for (SliceDescriptor sliceDescriptor : SliceDescriptor.ALL_SLICES) {
			int eventsCount = groupedData.get(sliceDescriptor).getEventsCount();
			allQuantityLevels.add(quantityLevelProvider.getLevelForQuantity(eventsCount));
		}

		List<LegendEntry> legendEntries = new ArrayList<>();

		for (QuantityLevel quantityLevel : allQuantityLevels) {
			Color colorForQuantity = HeatMapColorProvider.getColorForValue(quantityLevel.getLevelValue());
			legendEntries.add(new LegendEntry(quantityLevel.getLevelDescription(), colorForQuantity));
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

	public static Clock newInstance(List<ClockDataSet> clockDataSets) {
		return new Clock(clockDataSets);
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
}
