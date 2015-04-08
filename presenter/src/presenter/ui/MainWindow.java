package presenter.ui;

import histogram.view.Histogram;

import java.time.LocalDateTime;
import java.util.List;

import com.sun.xml.internal.ws.api.Cancelable;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
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
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Histogram test");
		
		VBox root = new VBox();

		LocalDateTime now = LocalDateTime.now();

		List<TimelineEvent> events = RandomDataGenerator.generateRandomEvents(now.minusYears(1), now, 10000);

		Histogram histogram = Histogram.newInstance(events);
		
		Canvas canvas = new Canvas(300, 300);
		
		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

		graphicsContext.strokePolyline(new double[] {20, 50, 50, 20}, new double[] {20, 50, 250, 280}, 4);
		graphicsContext.strokePolyline(new double[] {280, 250, 250, 280}, new double[] {20, 50, 250, 280}, 4);
		
		root.getChildren().addAll(histogram, canvas);

		primaryStage.setScene(new Scene(root, 1000, 1000));
		primaryStage.show();
	}
}
