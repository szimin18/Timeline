package presenter.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.agh.clock.model.ClockDataSet;
import pl.edu.agh.clock.model.IClockEvent;
import pl.edu.agh.clock.view.Clock;
import pl.edu.agh.clock.view.Clock.IClockSelectionListener;
import pl.edu.agh.clock.view.event.ClockSelectionEvent;
import presenter.generator.RandomDataGenerator;

public class ClockPresenter extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Clock test"); //$NON-NLS-1$

			int eventsPerDataset = 1000;

			long currentTimeMillis = System.currentTimeMillis();
			long timeRange = yearsInMiliseconds(1);
			List<ClockDataSet> timelineDataSets = new ArrayList<ClockDataSet>();
			timelineDataSets.add(RandomDataGenerator.generateClockDataSet(new Date(currentTimeMillis - timeRange),
					new Date(currentTimeMillis), eventsPerDataset));
			timelineDataSets.add(RandomDataGenerator.generateClockDataSet(new Date(currentTimeMillis - timeRange),
					new Date(currentTimeMillis), eventsPerDataset));
			timelineDataSets.add(RandomDataGenerator.generateClockDataSet(new Date(currentTimeMillis - timeRange),
					new Date(currentTimeMillis), eventsPerDataset));
			timelineDataSets.add(RandomDataGenerator.generateClockDataSet(new Date(currentTimeMillis - timeRange),
					new Date(currentTimeMillis), eventsPerDataset));
			timelineDataSets.add(RandomDataGenerator.generateClockDataSet(new Date(currentTimeMillis - timeRange),
					new Date(currentTimeMillis), eventsPerDataset));

			Clock clock = Clock.newInstance(timelineDataSets);

			clock.addSelectionListener(new IClockSelectionListener() {
				@Override
				public void selectionChanged(ClockSelectionEvent event) {
					System.out.println("Selection changed ------------------"); //$NON-NLS-1$
					System.out.println("Selected events count: " + event.getSelectedEventsCount()); //$NON-NLS-1$
					int i = 20;
					for (IClockEvent timelineEvent : event.getSelectedEvents()) {
						System.out.printf("%s\n", timelineEvent.getDate().toString()); //$NON-NLS-1$

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
