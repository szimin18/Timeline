package histogram.view;

import grouper.Grouper;
import grouper.Grouper.GroupingMethod;
import histogram.selector.Selector;
import histogram.selector.Selector.TimelineTick;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Transform;
import model.event.TimelineEvent;
import model.event.TimelineEventsGroup;

public class Histogram extends Pane {
	private final List<TimelineEvent> events;
	private final List<TimelineEventsGroup> groupedEvents;

	private Histogram(List<TimelineEvent> events, boolean isBarChart) {
		this.events = events;

		final StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.TOP_LEFT);
		getChildren().addAll(stackPane);

		widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				stackPane.setMinWidth(doubleValue);
				stackPane.setMaxWidth(doubleValue);
			};
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double doubleValue = newValue.doubleValue();
				stackPane.setMinHeight(doubleValue);
				stackPane.setMaxHeight(doubleValue);
			};
		});

		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		groupedEvents = Grouper.group(events, GroupingMethod.MONTHS);

		if (isBarChart) {
			final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

			final Selector selector = new Selector();

			stackPane.getChildren().addAll(barChart, selector);

			stackPane.widthProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					double doubleValue = newValue.doubleValue();
					barChart.setMinWidth(doubleValue);
					barChart.setMaxWidth(doubleValue);
					selector.widthProperty().set(doubleValue);
				};
			});

			stackPane.heightProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					double doubleValue = newValue.doubleValue();
					barChart.setMinHeight(doubleValue);
					barChart.setMaxHeight(doubleValue);
					selector.heightProperty().set(doubleValue);
				};
			});

			barChart.setTitle("Histogram");
			xAxis.setLabel("Event time ranges");
			yAxis.setLabel("Number of occurences");

			Series<String, Number> series = new Series<>();

			series.setName("events series");

			ObservableList<Data<String, Number>> seriesData = series.getData();
			for (TimelineEventsGroup eventsGroup : groupedEvents) {
				seriesData.add(new Data<String, Number>(eventsGroup.toString(), eventsGroup.getEventsCount()));
			}

			barChart.getData().add(series);

			series.getChart().getYAxis().localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
				public void changed(ObservableValue<? extends Transform> observable, Transform oldValue,
						Transform newValue) {
					selector.setTopY(newValue.getTy());
				};
			});

			series.getChart().getYAxis().heightProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					selector.setChartHeight(newValue.doubleValue());
				};
			});

			for (Data<String, Number> data : seriesData) {
				Node node = data.getNode();
				final TimelineTick timelineTick = selector.newTimelineTick();

				node.localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
					public void changed(ObservableValue<? extends Transform> observable, Transform oldValue,
							Transform newValue) {
						timelineTick.setLeft(newValue.getTx());
						selector.drawFrame();
					};
				});

				node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
					public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
						timelineTick.setWidth(newValue.getWidth());
						selector.drawFrame();
					};
				});
			}

			barChart.setBarGap(0);
			barChart.setCategoryGap(1);
		} else {
			//
			// LineChart<String, Number> lineChart = new LineChart<>(xAxis,
			// yAxis);
			//
			// add(lineChart, 0, 0);
			// setFillHeight(lineChart, true);
			// setFillWidth(lineChart, true);
			//
			// lineChart.setTitle("Histogram");
			// xAxis.setLabel("Event time ranges");
			// yAxis.setLabel("Number of occurences");
			//
			// Series<String, Number> series = new Series<>();
			//
			// series.setName("events series");
			//
			// groupedEvents.forEach(
			// eventsGroup -> series.getData().add(
			// new Data<String, Number>(eventsGroup.toString(),
			// eventsGroup.getEventsCount())));
			//
			// lineChart.getData().add(series);
		}
	}

	public static Histogram newInstance(List<TimelineEvent> events, boolean barChart) {
		return new Histogram(events, barChart);
	}
}
