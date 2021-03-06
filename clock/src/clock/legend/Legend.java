package clock.legend;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import clock.view.size.ISizeManagedNode;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

public abstract class Legend extends Canvas implements ISizeManagedNode {
	protected static final FontLoader FONT_LOADER = Toolkit.getToolkit().getFontLoader();

	protected static final double MARGIN = 5.0;

	private final List<LegendEntry> legendEntries;

	private final GraphicsContext graphicsContext = getGraphicsContext2D();

	private double fontSize = 0.0;

	protected Legend(List<LegendEntry> legendEntries) {
		this.legendEntries = legendEntries;

		ChangeListener<Number> drawFrameListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				drawFrame(graphicsContext);
			}
		};

		widthProperty().addListener(drawFrameListener);
		heightProperty().addListener(drawFrameListener);
	}

	@Override
	public void setSize(double newWidth, double newHeight, double newFontSize) {
		fontSize = newFontSize;

		setWidth(newWidth);
		setHeight(newHeight);

		drawFrame(graphicsContext);
	}

	@Override
	public Node getTopNode() {
		return this;
	}

	protected List<LegendEntry> getLegendEntries() {
		return legendEntries;
	}

	protected double getFontSize() {
		return fontSize;
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
