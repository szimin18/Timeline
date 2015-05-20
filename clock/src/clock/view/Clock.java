package clock.view;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import model.dataset.TimelineDataSet;

public class Clock extends Pane {
	private Clock(List<TimelineDataSet> timelineDataSets) {

		final StackPane stackPane = new StackPane();
		getChildren().addAll(stackPane);

		final PieChart chart = new PieChart();
		stackPane.getChildren().add(chart);

		chart.setLabelsVisible(false);
		chart.setLegendVisible(false);

		ObservableList<Data> chartData;
		chartData = chart.getData();

		Data data;

		data = new Data(null, 1);
		chartData.add(data);
		// data.getNode().setStyle("");

		data = new Data(null, 2);
		chartData.add(data);

		data = new Data(null, 3);
		chartData.add(data);

		final PieChart chart2 = new PieChart();
		stackPane.getChildren().add(chart2);

		chart2.setLabelsVisible(false);
		chart2.setLegendVisible(false);

		chartData = chart2.getData();

		data = new Data(null, 1);
		chartData.add(data);

		data = new Data(null, 2);
		chartData.add(data);

		data = new Data(null, 3);
		chartData.add(data);

		final PieChart chart3 = new PieChart();
		stackPane.getChildren().add(chart3);

		chart3.setLabelsVisible(false);
		chart3.setLegendVisible(false);

		chartData = chart3.getData();

		data = new Data(null, 1);
		chartData.add(data);
		data.getNode().setStyle("-fx-background-color: linear (100%,100%) to (100%,100%) stops (100%, yellow) (100%, yellow);");

		data = new Data(null, 2);
		chartData.add(data);

		data = new Data(null, 3);
		chartData.add(data);

		final PieChart chart4 = new PieChart();
		stackPane.getChildren().add(chart4);

		chart4.setLabelsVisible(false);
		chart4.setLegendVisible(false);

		chartData = chart4.getData();

		data = new Data(null, 1);
		chartData.add(data);
		data.getNode().setVisible(false);

		data = new Data(null, 2);
		chartData.add(data);

		data = new Data(null, 3);
		chartData.add(data);

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				chart.setMinWidth(value);
				chart.setMaxWidth(value);
				chart2.setMinWidth(value * 3 / 4);
				chart2.setMaxWidth(value * 3 / 4);
				chart3.setMinWidth(value / 2);
				chart3.setMaxWidth(value / 2);
				chart4.setMinWidth(value / 4);
				chart4.setMaxWidth(value / 4);
			}
		});
		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = newValue.doubleValue();
				chart.setMinHeight(value);
				chart.setMaxHeight(value);
				chart2.setMinHeight(value * 3 / 4);
				chart2.setMaxHeight(value * 3 / 4);
				chart3.setMinHeight(value / 2);
				chart3.setMaxHeight(value / 2);
				chart4.setMinHeight(value / 4);
				chart4.setMaxHeight(value / 4);
			}
		});

	}

	public static Clock newInstance(List<TimelineDataSet> timelineDataSets) {
		return new Clock(timelineDataSets);
	}
}
