package clock.view;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.dataset.TimelineDataSet;
import model.event.TimelineChartData;
import clock.grouper.Grouper;
import clock.util.DayOfWeek;

public class Clock extends Pane {
	private static final double ONE_SEVENTH = 1.0 / 7.0;
	private static final Color DEFAULT_CHART_BASE_COLOR = Color.GOLD;
	private static final double CHART_MINIMUM_BRIGHTNESS = 20.0;
	private static final double CHART_MAXIMUM_BRIGHTNESS = 100.0;
	private static final double CHART_BRIGHTNESS_RANGE = CHART_MAXIMUM_BRIGHTNESS - CHART_MINIMUM_BRIGHTNESS;

	private Clock(List<TimelineDataSet> timelineDataSets, Color chartBaseColor) {
		final StackPane stackPane = new StackPane();
		getChildren().addAll(stackPane);

		Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData = Grouper.group(timelineDataSets);

		int chartSizeIndex = 7;

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

		if (maximumEventsCount == 0) {
			throw new AssertionError();
		}

		int eventsCountRange = maximumEventsCount - minimumEventsCount;

		double chartBaseHue = chartBaseColor.getHue();
		double chartBaseSaturation = chartBaseColor.getSaturation() * 100;

		String pieColorStyleTemplate = String.format(Locale.ENGLISH,
				"-fx-pie-color: hsb(%f, %f%%%%, %%f%%%%); -fx-border-color: derive(-fx-pie-color, -40%%%%);",
				chartBaseHue, chartBaseSaturation);

		final Set<Node> allNodes = new HashSet<>();

		stackPane.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for (Node node : allNodes) {
					if (node.contains(event.getX() - stackPane.widthProperty().doubleValue() / 2, event.getY()
							- stackPane.heightProperty().doubleValue() / 2)) {
						node.setEffect(new Lighting());
					} else {
						node.setEffect(null);
					}
				}
			}
		});

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			final PieChart chart = new PieChart();
			stackPane.getChildren().add(chart);

			chart.setLabelsVisible(false);
			chart.setLegendVisible(false);

			final double chartFactor = 1.0 * chartSizeIndex * ONE_SEVENTH;

			widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					double value = newValue.doubleValue();
					chart.setMinWidth(value * chartFactor);
					chart.setMaxWidth(value * chartFactor);
				}
			});

			heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					double value = newValue.doubleValue();
					chart.setMinHeight(value * chartFactor);
					chart.setMaxHeight(value * chartFactor);
				}
			});

			ObservableList<Data> chartData = chart.getData();
			for (int hour = 0; hour < 24; hour++) {
				TimelineChartData timelineChartData = groupedData.get(dayOfWeek).get((hour + 6) % 24);
				Data data = new Data(null, 1);
				chartData.add(data);
				double nodeBrightness = CHART_MAXIMUM_BRIGHTNESS
						- (((double) (timelineChartData.getEventsCount() - minimumEventsCount) / eventsCountRange) * CHART_BRIGHTNESS_RANGE);
				String format = String.format(Locale.ENGLISH, pieColorStyleTemplate, nodeBrightness);
				System.out.println(format);
				final Node node = data.getNode();
				node.setStyle(format);
				// data.getNode().setStyle("-fx-border-width: 1px;");

				Tooltip.install(node, new Tooltip(timelineChartData.getDescription()));

				allNodes.add(node);

				// node.hoverProperty().addListener(new
				// ChangeListener<Boolean>() {
				// @Override
				// public void changed(ObservableValue<? extends Boolean>
				// observable, Boolean oldValue,
				// Boolean newValue) {
				// if (newValue) {
				// node.setEffect(new Lighting());
				// } else {
				// node.setEffect(null);
				// }
				// }
				// });

				//
				// node.setOnMouseMoved(new EventHandler<MouseEvent>() {
				// @Override
				// public void handle(MouseEvent event) {
				// node.setEffect(new Lighting());
				// }
				// });
				// node.setOnMouseExited(new EventHandler<MouseEvent>() {
				// @Override
				// public void handle(MouseEvent event) {
				// node.setEffect(null);
				// }
				// });
			}

			chartSizeIndex--;
		}
	}

	public static Clock newInstance(List<TimelineDataSet> timelineDataSets) {
		return new Clock(timelineDataSets, DEFAULT_CHART_BASE_COLOR);
	}

	public static Clock newInstance(List<TimelineDataSet> timelineDataSets, Color chartBaseColor) {
		return new Clock(timelineDataSets, chartBaseColor);
	}
}
