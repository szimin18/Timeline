package clock.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.dataset.TimelineDataSet;
import model.event.TimelineChartData;
import clock.grouper.Grouper;
import clock.grouper.QuantityLeveler;
import clock.grouper.QuantityLeveler.QuantityLevel;
import clock.grouper.QuantityLeveler.QuantityLevelProvider;
import clock.selector.Selector;
import clock.selector.Selector.ICanvasListener;
import clock.util.DayOfWeek;

public class Clock extends Pane {
	private static final double ONE_NINTH = 1.0 / 9.0;
	private static final Color DEFAULT_CHART_BASE_COLOR = Color.GOLD;
	private static final double CHART_MINIMUM_BRIGHTNESS = 20.0;
	private static final double CHART_MAXIMUM_BRIGHTNESS = 100.0;

	private static final Effect LIGHTNING_EFFECT = new Lighting();

	private Node hoveredNode = null;

	private Node selectedNode = null;

	private final List<IClockSelectionListener> selectionListeners = new ArrayList<>();

	private Clock(List<TimelineDataSet> timelineDataSets, Color chartBaseColor) {
		final StackPane stackPane = new StackPane();
		getChildren().addAll(stackPane);

		final Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData = Grouper.group(timelineDataSets);

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

		// final Set<Node> allNodes = new HashSet<>();
		//
		// stackPane.addEventHandler(MouseEvent.MOUSE_MOVED, new
		// EventHandler<MouseEvent>() {
		// @Override
		// public void handle(MouseEvent event) {
		// for (Node node : allNodes) {
		// if (node.contains(event.getX() -
		// stackPane.widthProperty().doubleValue() / 2, event.getY()
		// - stackPane.heightProperty().doubleValue() / 2)) {
		// node.setEffect(new Lighting());
		// } else {
		// node.setEffect(null);
		// }
		// }
		// }
		// });

		double chartBaseHue = chartBaseColor.getHue();
		double chartBaseSaturation = chartBaseColor.getSaturation() * 100;

		String pieColorStyleTemplate = String.format(Locale.ENGLISH,
				"-fx-pie-color: hsb(%f, %f%%%%, %%f%%%%); -fx-border-color: derive(-fx-pie-color, -40%%%%);",
				chartBaseHue, chartBaseSaturation);

		QuantityLevelProvider quantityLevelProvider = QuantityLeveler.getQuantityLevelProvider(
				CHART_MAXIMUM_BRIGHTNESS, CHART_MINIMUM_BRIGHTNESS, minimumEventsCount, maximumEventsCount);

		int chartSizeIndex = 9;

		final Map<DayOfWeek, Map<Integer, Node>> allChartNodes = new HashMap<>(DayOfWeek.VALUES_LIST.size());
		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			allChartNodes.put(dayOfWeek, new HashMap<Integer, Node>(24));
		}

		PieChart chartForSelector = null;
		Node nodeForSelector = null;

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			final PieChart chart = new PieChart();
			stackPane.getChildren().add(chart);

			chart.setLabelsVisible(false);
			chart.setLegendVisible(false);

			final double chartFactor = ONE_NINTH * chartSizeIndex;

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
			for (int hour = 6;; hour++) {
				if (hour == 24) {
					hour -= 24;
				}

				Data data = new Data(null, 1);
				chartData.add(data);
				QuantityLevel levelForQuantity = quantityLevelProvider.getLevelForQuantity(groupedData.get(dayOfWeek)
						.get(hour).getEventsCount());
				String format = String.format(Locale.ENGLISH, pieColorStyleTemplate, levelForQuantity.getLevelValue());
				System.out.println(format);
				final Node node = data.getNode();
				node.setStyle(format);

				if (dayOfWeek == DayOfWeek.MONDAY && hour == 23) {
					nodeForSelector = node;
					chartForSelector = chart;
				}

				// Tooltip.install(node, new
				// Tooltip(timelineChartData.getDescription()));

				allChartNodes.get(dayOfWeek).put(hour, node);

				if (hour == 5) {
					break;
				}
			}
			chartSizeIndex--;
		}

		final PieChart whiteChart = new PieChart();
		stackPane.getChildren().add(whiteChart);

		whiteChart.setLabelsVisible(false);
		whiteChart.setLegendVisible(false);

		final double chartFactor = ONE_NINTH * chartSizeIndex;

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				whiteChart.setMinWidth(value * chartFactor);
				whiteChart.setMaxWidth(value * chartFactor);
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				whiteChart.setMinHeight(value * chartFactor);
				whiteChart.setMaxHeight(value * chartFactor);
			}
		});

		Data data = new Data(null, 1);
		whiteChart.getData().add(data);
		data.getNode().setStyle("-fx-background-color: rgb(255, 255, 255); -fx-border-color: rgb(255, 255, 255);");

		final Selector selector = new Selector(nodeForSelector);
		stackPane.getChildren().add(selector);

		chartForSelector.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				selector.setWidth(newValue.doubleValue());
			}
		});
		chartForSelector.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				selector.setHeight(newValue.doubleValue());
			}
		});

		selector.addCanvasListener(new ICanvasListener() {
			@Override
			public void hoverRemoved() {
				if (hoveredNode != null && hoveredNode != selectedNode) {
					hoveredNode.setEffect(null);
				}
			}

			@Override
			public void hoverChanged(DayOfWeek dayOfWeek, int hour) {
				Node newHoveredNode = allChartNodes.get(dayOfWeek).get(hour);
				if (newHoveredNode != hoveredNode) {
					hoverRemoved();
					hoveredNode = newHoveredNode;
					hoveredNode.setEffect(LIGHTNING_EFFECT);
				}
			}

			@Override
			public void selectionRemoved() {
				if (selectedNode != null && selectedNode != hoveredNode) {
					selectedNode.setEffect(null);
				}
				notifySelectionRemoved();
			}

			@Override
			public void selectionChanged(DayOfWeek dayOfWeek, int hour) {
				Node newSelectedNode = allChartNodes.get(dayOfWeek).get(hour);
				if (newSelectedNode != selectedNode) {
					selectionRemoved();
					selectedNode = newSelectedNode;
					selectedNode.setEffect(LIGHTNING_EFFECT);
				}
				notifySelectionChanged(dayOfWeek, groupedData.get(dayOfWeek).get(hour).getDescription());
			}
		});
	}

	public static Clock newInstance(List<TimelineDataSet> timelineDataSets) {
		return new Clock(timelineDataSets, DEFAULT_CHART_BASE_COLOR);
	}

	public static Clock newInstance(List<TimelineDataSet> timelineDataSets, Color chartBaseColor) {
		return new Clock(timelineDataSets, chartBaseColor);
	}

	private void notifySelectionRemoved() {
		for (IClockSelectionListener listener : selectionListeners) {
			listener.selectionRemoved();
		}
	}

	private void notifySelectionChanged(DayOfWeek dayOfWeek, String hourDescription) {
		for (IClockSelectionListener listener : selectionListeners) {
			listener.selectionChanged(dayOfWeek, hourDescription);
		}
	}

	public void addSelectionListener(IClockSelectionListener listener) {
		selectionListeners.add(listener);
	}

	public void removeSelectionListener(IClockSelectionListener listener) {
		selectionListeners.remove(listener);
	}

	public static interface IClockSelectionListener {
		public void selectionRemoved();

		public void selectionChanged(DayOfWeek dayOfWeek, String hourDescription);
	}
}
