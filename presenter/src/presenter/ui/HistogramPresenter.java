package presenter.ui;

import histogram.event.HistogramFilterChangeEvent;
import histogram.event.HistogramFilterChangeListener;
import histogram.grouper.Grouper.GroupingMethod;
import histogram.view.Histogram;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.dataset.TimelineDataSet;
import presenter.generator.RandomDataGenerator;

public class HistogramPresenter extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		/*
		 * TODO
		 * 
		 * - add grouping methods
		 * - listeners on node selected
		 * - multi node selection
		 */
		
		try {
			primaryStage.setTitle("Histogram test");

			long currentTimeMillis = System.currentTimeMillis();
		
			long timeRange = yearsInMiliseconds(1)/12;	
			
			List<TimelineDataSet> timelineDataSets = new ArrayList<TimelineDataSet>();
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.GOLD));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.AQUA));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.GREEN));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.BLUE));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.RED));

			Histogram histogram = new Histogram(timelineDataSets);

			histogram.addFilterChangeListener(new HistogramFilterChangeListener() {
				@Override
				public void selectionChanged(HistogramFilterChangeEvent event) {
					System.out.printf("Selection changed: %s - %s\n", event.getBeginning(), event.getEnd());
				}
			});

			primaryStage.setScene(new Scene(histogram, 1400, 400));
			primaryStage.show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private long yearsInMiliseconds(int years) {
		return (long) (years * 365.25 * 24 * 60 * 60 * 1000);
	}
}
