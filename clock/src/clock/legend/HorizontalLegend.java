package clock.legend;

import java.util.List;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.canvas.GraphicsContext;

public final class HorizontalLegend extends Legend {
	public HorizontalLegend(List<LegendEntry> legendEntries, ReadOnlyDoubleProperty fontSizeProperty) {
		super(legendEntries, fontSizeProperty);
	}

	@Override
	protected void drawFrame(GraphicsContext graphicsContext) {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

	}
}
