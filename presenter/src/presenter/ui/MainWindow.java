package presenter.ui;

import histogram.view.Histogram;

import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.event.TimelineEvent;
import presenter.generator.RandomDataGenerator;

public class MainWindow extends Application {

	public static void main(String[] args) {
		launch();
	}

	public MainWindow() {
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Histogram test");

			long currentTimeMillis = System.currentTimeMillis();
			List<TimelineEvent> events = RandomDataGenerator.generateRandomEvents(new Date(currentTimeMillis
					- yearsInMiliseconds(1)), new Date(currentTimeMillis), 10000);

			Histogram histogram = Histogram.newInstance(events, true);

			primaryStage.setScene(new Scene(histogram, 1400, 400));
			primaryStage.show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private int yearsInMiliseconds(int years) {
		return (int)(years * 365.25 * 24 * 60 * 60 * 1000);
	}
}
