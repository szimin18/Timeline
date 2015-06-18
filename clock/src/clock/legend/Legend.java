package clock.legend;

import java.util.List;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class Legend extends Canvas {
	protected final List<LegendEntry> legendEntries;

	private ReadOnlyDoubleProperty fontSizeProperty;

	private final GraphicsContext graphicsContext = getGraphicsContext2D();

	protected Legend(List<LegendEntry> legendEntries, ReadOnlyDoubleProperty fontSizeProperty) {
		this.legendEntries = legendEntries;
		this.fontSizeProperty = fontSizeProperty;

		ChangeListener<Number> drawFrameListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				drawFrame(graphicsContext);
			}
		};

		widthProperty().addListener(drawFrameListener);
		heightProperty().addListener(drawFrameListener);
		fontSizeProperty.addListener(drawFrameListener);
	}

	protected Font getFont() {
		return new Font(fontSizeProperty.get());
	}

	protected abstract void drawFrame(GraphicsContext graphicsContext);

	public static final class LegendEntry {
		private final String description;

		private final Color color;

		public LegendEntry(String description, Color color) {
			this.description = description;
			this.color = color;
		}

		public String getDescription() {
			return description;
		}

		public Color getColor() {
			return color;
		}
	}
}
