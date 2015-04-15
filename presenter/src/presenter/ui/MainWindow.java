package presenter.ui;

import histogram.view.Histogram;

import java.time.LocalDateTime;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.event.TimelineEvent;
import presenter.generator.RandomDataGenerator;
import presenter.selector.Selector;

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
		
		Selector selector = new Selector(500, 227, 27);
		
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.TOP_LEFT);
		stackPane.getChildren().addAll(histogram, selector);
		root.getChildren().add(stackPane);

		primaryStage.setScene(new Scene(root, 800, 800));
		primaryStage.show();
	}
}
