package clock.legend;

import java.util.List;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import clock.view.size.MinimumSizeManager;

import com.google.common.base.Function;
import com.sun.javafx.tk.FontMetrics;

public final class HorizontalLegend extends Legend {
	private final MinimumSizeManager minimumSizeManager = new MinimumSizeManager(new Function<Double, Dimension2D>() {
		@Override
		public Dimension2D apply(Double fontSize) {
			FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(new Font(fontSize));

			double lineHeight = fontMetrics.getLineHeight();

			double totalWidth = Math.max(0, getLegendEntries().size() * lineHeight * 4 - lineHeight);

			for (LegendEntry legendEntry : getLegendEntries()) {
				totalWidth += fontMetrics.computeStringWidth(legendEntry.getDescription());
			}

			return new Dimension2D(2 * MARGIN + totalWidth, 2 * MARGIN + lineHeight * 3 / 2);
		}
	});

	public HorizontalLegend(List<LegendEntry> legendEntries) {
		super(legendEntries);
	}
	
	@Override
	public Dimension2D getMinimumSize(double fontSize) {
		return minimumSizeManager.getMinimumSize(fontSize);
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

		double currentXPosition = (getWidth() - minimumSizeManager.getMinimumSize(fontSize).getWidth()) / 2;
		double marginForText = MARGIN + lineHeight;

		for (LegendEntry legendEntry : getLegendEntries()) {
			String legendEntryDescription = legendEntry.getDescription();

			graphicsContext.setFill(legendEntry.getColor());

			graphicsContext.fillRect(currentXPosition, MARGIN, lineHeight * 2, lineHeight * 3 / 2);
			graphicsContext.strokeRect(currentXPosition, MARGIN, lineHeight * 2, lineHeight * 3 / 2);

			currentXPosition += 3 * lineHeight;

			graphicsContext.setFill(Color.BLACK);

			graphicsContext.fillText(legendEntryDescription, currentXPosition, marginForText);
			graphicsContext.strokeText(legendEntryDescription, currentXPosition, marginForText);

			currentXPosition += lineHeight + fontMetrics.computeStringWidth(legendEntryDescription);
		}
	}
}
