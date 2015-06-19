package clock.legend;

import java.util.List;

import com.sun.javafx.tk.FontMetrics;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class HorizontalLegend extends Legend {
	public HorizontalLegend(List<LegendEntry> legendEntries, ReadOnlyDoubleProperty fontSizeProperty) {
		super(legendEntries, fontSizeProperty);
	}

	@Override
	protected void drawFrame(GraphicsContext graphicsContext) {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		FontMetrics fontMetrics = getFontMetrics();

		double lineHeight = fontMetrics.getLineHeight();

		Font font = getFont();

		graphicsContext.setFont(font);
		graphicsContext.setStroke(Color.BLACK);

		double totalWidth = Math.max(0, getLegendEntries().size() * lineHeight * 4 - lineHeight);

		for (LegendEntry legendEntry : getLegendEntries()) {
			totalWidth += fontMetrics.computeStringWidth(legendEntry.getDescription());
		}

		double currentXPosition = (getWidth() - totalWidth) / 2;
		double currentYPosition = 10;

		for (LegendEntry legendEntry : getLegendEntries()) {
			String legendEntryDescription = legendEntry.getDescription();

			graphicsContext.setFill(legendEntry.getColor());

			graphicsContext.fillRect(currentXPosition, currentYPosition, lineHeight * 2, lineHeight * 3 / 2);
			graphicsContext.strokeRect(currentXPosition, currentYPosition, lineHeight * 2, lineHeight * 3 / 2);

			currentXPosition += 3 * lineHeight;

			graphicsContext.setFill(Color.BLACK);

			graphicsContext.fillText(legendEntryDescription, currentXPosition, currentYPosition + lineHeight);
			graphicsContext.strokeText(legendEntryDescription, currentXPosition, currentYPosition + lineHeight);

			currentXPosition += lineHeight + fontMetrics.computeStringWidth(legendEntryDescription);
		}
	}
}
