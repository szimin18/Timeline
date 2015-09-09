package clock.legend;

import java.util.List;
import java.util.Map;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import clock.view.font.FontSizeManager;

import com.google.common.collect.Maps;
import com.sun.javafx.tk.FontMetrics;

public final class VerticalLegend extends Legend {
	private final Map<Double, Dimension2D> minimumChartSizes = Maps.newHashMap();

	public VerticalLegend(List<LegendEntry> legendEntries) {
		super(legendEntries);

		for (double fontSize = FontSizeManager.MAX_FONT_SIZE; fontSize >= 0; fontSize -= FontSizeManager.FONT_SIZE_DELTA) {
			FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(new Font(fontSize));

			double lineHeight = fontMetrics.getLineHeight();

			double totalHeight = Math.max(2 * getLegendEntries().size() - 1, 0) * lineHeight * 3 / 2;

			double totalWidth = 0.0;

			for (LegendEntry legendEntry : getLegendEntries()) {
				totalWidth = Math.max(totalWidth, fontMetrics.computeStringWidth(legendEntry.getDescription()));
			}

			totalWidth += 3 * lineHeight;

			minimumChartSizes.put(fontSize, new Dimension2D(2 * MARGIN + totalWidth, 2 * MARGIN + totalHeight));
		}
	}

	@Override
	public double getMaximumFontSize(double width, double height) {
		for (double currentFontSize = FontSizeManager.MAX_FONT_SIZE;; currentFontSize -= FontSizeManager.FONT_SIZE_DELTA) {
			if (currentFontSize <= FontSizeManager.MIN_FONT_SIZE) {
				return currentFontSize;
			}

			Dimension2D minimumSize = minimumChartSizes.get(currentFontSize);
			if (width >= minimumSize.getWidth() && height >= minimumSize.getHeight()) {
				return currentFontSize;
			}
		}
	}

	@Override
	protected void drawFrame(GraphicsContext graphicsContext) {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		double fontSize = getFontSize();
		Font font = new Font(fontSize);
		FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(font);

		double lineHeight = fontMetrics.getLineHeight();

		graphicsContext.setFont(font);
		graphicsContext.setStroke(Color.BLACK);

		double marginForText = MARGIN + 3 * lineHeight;
		double currentYPosition = (getHeight() - minimumChartSizes.get(fontSize).getHeight()) / 2;

		for (LegendEntry legendEntry : getLegendEntries()) {
			String legendEntryDescription = legendEntry.getDescription();

			graphicsContext.setFill(legendEntry.getColor());

			graphicsContext.fillRect(MARGIN, currentYPosition, lineHeight * 2, lineHeight * 3 / 2);
			graphicsContext.strokeRect(MARGIN, currentYPosition, lineHeight * 2, lineHeight * 3 / 2);

			graphicsContext.setFill(Color.BLACK);

			graphicsContext.fillText(legendEntryDescription, marginForText, currentYPosition + lineHeight);
			graphicsContext.strokeText(legendEntryDescription, marginForText, currentYPosition + lineHeight);

			currentYPosition += 3 * lineHeight;
		}
	}
}
