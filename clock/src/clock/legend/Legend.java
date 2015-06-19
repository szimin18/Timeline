package clock.legend;

import java.util.List;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class Legend extends Canvas {
	private static final FontLoader FONT_LOADER = Toolkit.getToolkit().getFontLoader();

	private final List<LegendEntry> legendEntries;

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

	protected List<LegendEntry> getLegendEntries() {
		return legendEntries;
	}

	protected Font getFont() {
		return new Font(fontSizeProperty.get());
	}

	protected FontMetrics getFontMetrics() {
		return FONT_LOADER.getFontMetrics(getFont());
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
