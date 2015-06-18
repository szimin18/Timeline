package presenter.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.dataset.TimelineDataSet;
import model.event.TimelineEvent;
import presenter.generator.RandomDataGenerator;
import clock.view.Clock;
import clock.view.Clock.ClockSelectionEvent;
import clock.view.Clock.IClockSelectionListener;

public class ClockPresenter extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Clock test");

			long currentTimeMillis = System.currentTimeMillis();
			long timeRange = yearsInMiliseconds(1);
			List<TimelineDataSet> timelineDataSets = new ArrayList<TimelineDataSet>();
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.GOLD));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.GREEN));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.GREEN));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.BLUE));
			timelineDataSets.add(RandomDataGenerator.generateDataSet(new Date(currentTimeMillis - timeRange), new Date(
					currentTimeMillis), 100, Color.RED));

			Clock clock = Clock.newInstance(timelineDataSets);

			clock.addSelectionListener(new IClockSelectionListener() {
				@Override
				public void selectionChanged(ClockSelectionEvent event) {
					System.out.println("Selection changed ------------------");
					System.out.println("Selected events count: " + event.getSelectedEventsCount());
					int i = 20;
					for (TimelineEvent timelineEvent : event.getSelectedEvents()) {
						System.out.printf("%s # %s\n", timelineEvent.getTid(), timelineEvent.getDate().toString());

						if (--i <= 0) {
							break;
						}
					}
				}
			});

			primaryStage.setScene(new Scene(clock, 600, 600));
			primaryStage.show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private long yearsInMiliseconds(int years) {
		return (long) (years * 365.25 * 24 * 60 * 60 * 1000);
	}
}
