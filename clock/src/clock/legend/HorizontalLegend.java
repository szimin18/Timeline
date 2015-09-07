package clock.legend;

import java.util.List;
import java.util.Map;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import clock.view.font.FontSizeManager;

import com.google.common.collect.Maps;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public final class HorizontalLegend extends Legend {
	private static final FontLoader FONT_LOADER = Toolkit.getToolkit().getFontLoader();

	private static final double TOP_BOTTOM_MARGIN = 10.0;

	private final Map<Double, Dimension2D> minimumChartSizes = Maps.newHashMap();

	public HorizontalLegend(List<LegendEntry> legendEntries) {
		super(legendEntries);

		for (double fontSize = FontSizeManager.MAX_FONT_SIZE; fontSize >= 0; fontSize -= FontSizeManager.FONT_SIZE_DELTA) {
			FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(new Font(fontSize));

			double lineHeight = fontMetrics.getLineHeight();

			double totalWidth = Math.max(0, getLegendEntries().size() * lineHeight * 4 - lineHeight);

			for (LegendEntry legendEntry : getLegendEntries()) {
				totalWidth += fontMetrics.computeStringWidth(legendEntry.getDescription());
			}

			minimumChartSizes.put(fontSize, new Dimension2D(totalWidth, 2 * TOP_BOTTOM_MARGIN + lineHeight));
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

		double currentXPosition = (getWidth() - minimumChartSizes.get(fontSize).getWidth()) / 2;
		double currentYPosition = TOP_BOTTOM_MARGIN;

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
