package clock.legend;

import java.util.List;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import clock.view.font.MinimumSizeManager;

import com.google.common.base.Function;
import com.sun.javafx.tk.FontMetrics;

public final class VerticalLegend extends Legend {
	private final MinimumSizeManager minimumSizeManager = new MinimumSizeManager(new Function<Double, Dimension2D>() {
		@Override
		public Dimension2D apply(Double fontSize) {
			FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(new Font(fontSize));

			double lineHeight = fontMetrics.getLineHeight();

			double totalHeight = Math.max(2 * getLegendEntries().size() - 1, 0) * lineHeight * 3 / 2;

			double totalWidth = 0.0;

			for (LegendEntry legendEntry : getLegendEntries()) {
				totalWidth = Math.max(totalWidth, fontMetrics.computeStringWidth(legendEntry.getDescription()));
			}

			totalWidth += 3 * lineHeight;

			return new Dimension2D(2 * MARGIN + totalWidth, 2 * MARGIN + totalHeight);
		}
	});

	public VerticalLegend(List<LegendEntry> legendEntries) {
		super(legendEntries);
	}

	@Override
	public MinimumSizeManager getMinimumSizeManager() {
		return minimumSizeManager;
	}

	@Override
	protected void drawFrame(GraphicsContext graphicsContext) {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		double fontSize = getFontSize();
		Font font = new Font(fontSize);
		
		double lineHeight = FONT_LOADER.getFontMetrics(font).getLineHeight();

		graphicsContext.setFont(font);
		graphicsContext.setStroke(Color.BLACK);

		double marginForText = MARGIN + 3 * lineHeight;
		double currentYPosition = (getHeight() - minimumSizeManager.getMinimumSize(fontSize).getHeight()) / 2;

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
