package clock.view.chart.popup;

import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;
import javafx.stage.Screen;
import javafx.stage.Window;

public class ChartPopup {

	private static final double CURSOR_WIDTH = 15.0;

	private static final double CURSOR_HEIGHT = 20.0;

	private final Label popupLabel;

	private final Popup popup;

	public ChartPopup() {
		popupLabel = new Label();
		popupLabel.setStyle("-fx-background-color: white;-fx-border-color: black");
		popup = PopupBuilder.create().content(popupLabel).width(200).height(50).autoFix(true).build();
	}

	public void show(Window window, double screenX, double screenY, String description) {
		popup.show(window);
		popupLabel.setText(description);

		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();

		popup.setX(Math.min(screenX + CURSOR_WIDTH, screenWidth - popupLabel.getWidth()));
		popup.setY(screenY + CURSOR_HEIGHT);
	}

	public void hide() {
		popup.hide();
	}
}
