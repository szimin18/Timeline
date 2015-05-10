package histogram.view;

import grouper.Grouper;
import grouper.Grouper.GroupingMethod;
import histogram.selector.Selector;
import histogram.selector.Selector.TimelineTick;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import model.dataset.TimelineDataSet;
import model.event.TimelineCategory;
import model.event.TimelineChartData;

public class Histogram extends Pane {

	private final GroupingMethod defaultGroupingMethod;

	private GroupingMethod groupingMethod;

	private StackPane currentStackPane = null;

	private ChangeListener<Number> widthChangeListener = null;

	private ChangeListener<Number> heightchangeListener = null;

	private final List<TimelineDataSet> timelineDataSets;

	private Histogram(List<TimelineDataSet> timelineDataSets) {
		this.timelineDataSets = timelineDataSets;

		defaultGroupingMethod = GroupingMethod.defaultForDatasets(timelineDataSets);

		groupingMethod = defaultGroupingMethod;

		initializeGUI();
	}

	private Histogram(List<TimelineDataSet> timelineDataSets, GroupingMethod groupingMethod) {
		this.timelineDataSets = timelineDataSets;

		defaultGroupingMethod = GroupingMethod.defaultForDatasets(timelineDataSets);

		this.groupingMethod = groupingMethod;

		initializeGUI();
	}

	public void setGroupingMethod(GroupingMethod groupingMethod) {
		this.groupingMethod = groupingMethod;
	}

	public void restoreDefaultGroupingMethod() {
		groupingMethod = defaultGroupingMethod;
	}

	private void initializeGUI() {
		if (widthChangeListener != null) {
			widthProperty().removeListener(widthChangeListener);
			widthChangeListener = null;
		}
		if (heightchangeListener != null) {
			heightProperty().removeListener(heightchangeListener);
			heightchangeListener = null;
		}
		if (currentStackPane != null) {
			getChildren().removeAll(currentStackPane);
			currentStackPane = null;
		}

		List<TimelineCategory> groupedCategories = Grouper.group(timelineDataSets, groupingMethod);

		final StackPane stackPane = new StackPane();
		getChildren().addAll(stackPane);
		currentStackPane = stackPane;
		stackPane.setAlignment(Pos.TOP_LEFT);

		widthChangeListener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				stackPane.setMinWidth(doubleValue);
				stackPane.setMaxWidth(doubleValue);
			};
		};
		widthProperty().addListener(widthChangeListener);

		heightchangeListener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				stackPane.setMinHeight(doubleValue);
				stackPane.setMaxHeight(doubleValue);
			};
		};
		heightProperty().addListener(heightchangeListener);

		final Selector selector = new Selector();

		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		final StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);

		chart.setCategoryGap(1);
		chart.setLegendVisible(false);

		stackPane.getChildren().addAll(chart, selector);

		stackPane.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				chart.setMinWidth(doubleValue);
				chart.setMaxWidth(doubleValue);
				selector.widthProperty().set(doubleValue);
			};
		});

		stackPane.heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				chart.setMinHeight(doubleValue);
				chart.setMaxHeight(doubleValue);
				selector.heightProperty().set(doubleValue);
			};
		});

		chart.getXAxis().localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
			@Override
			public void changed(ObservableValue<? extends Transform> observable, Transform oldValue, Transform newValue) {
				selector.setLeftX(newValue.getTx());
			}
		});

		chart.getXAxis().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				selector.setChartWidth(newValue.doubleValue());
			}
		});

		chart.getYAxis().localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
			public void changed(ObservableValue<? extends Transform> observable, Transform oldValue, Transform newValue) {
				selector.setTopY(newValue.getTy());
			};
		});

		chart.getYAxis().heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				selector.setChartHeight(newValue.doubleValue());
			};
		});

		ObservableList<String> categoriesNamesList = FXCollections.<String> observableArrayList();

		xAxis.setCategories(categoriesNamesList);

		chart.setTitle("Histogram");
		xAxis.setLabel("Event time periods");
		yAxis.setLabel("Number of occurences");

		ObservableList<Series<String, Number>> chartData = chart.getData();

		boolean firstSeries = true;

		for (TimelineCategory timelineCategory : groupedCategories) {
			Series<String, Number> series = new Series<>();

			ObservableList<Data<String, Number>> seriesData = series.getData();

			chartData.add(series);

			for (TimelineChartData timelineChartData : timelineCategory.getTimelineChartDataList()) {
				Data<String, Number> data = new Data<String, Number>(timelineChartData.getDescription(),
						timelineChartData.getEventsCount());
				seriesData.add(data);

				Node node = data.getNode();
				node.setStyle(String.format("-fx-bar-fill: %s;", timelineCategory.getColorHex()));

				if (firstSeries) {
					categoriesNamesList.add(data.getXValue());

					final TimelineTick timelineTick = selector.newTimelineTick();

					node.localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
						public void changed(ObservableValue<? extends Transform> observable, Transform oldValue,
								Transform newValue) {
							timelineTick.setLeft(newValue.getTx());
							selector.drawFrame();
						};
					});

					node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
						public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue,
								Bounds newValue) {
							timelineTick.setWidth(newValue.getWidth());
							selector.drawFrame();
						};
					});
				}
			}

			firstSeries = false;
		}
	}

	public static Histogram newInstance(List<TimelineDataSet> timelineDataSets) {
		return new Histogram(timelineDataSets);
	}
}
