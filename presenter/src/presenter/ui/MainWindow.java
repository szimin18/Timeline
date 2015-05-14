package presenter.ui;

import grouper.Grouper.GroupingMethod;
import histogram.view.Histogram;
import histogram.view.HistogramSelectionChangeEvent;
import histogram.view.HistogramSelectionChangeListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.dataset.TimelineDataSet;
import presenter.generator.RandomDataGenerator;

public class MainWindow extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		/*
		 * TODO
		 * 
		 * - listeners
		 */
		
		try {
			primaryStage.setTitle("Histogram test");

			long currentTimeMillis = System.currentTimeMillis();
//			long timeRange = yearsInMiliseconds(5);			//MONTHS
			long timeRange = yearsInMiliseconds(1);			//WEEKS
//			long timeRange = yearsInMiliseconds(1)/12;		//DAYS
//			long timeRange = yearsInMiliseconds(1)/60;		//HOURS
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
			histogram.setGroupingMethod(GroupingMethod.DAYS);
			
			histogram.addSelectionChangeListener(new HistogramSelectionChangeListener() {
				@Override
				public void selectionChanged(HistogramSelectionChangeEvent event) {
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
