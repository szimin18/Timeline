package pl.edu.agh.clock.view.chart.visual;

import com.google.common.base.Function;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import pl.edu.agh.clock.math.MathChartHelper;
import pl.edu.agh.clock.model.DayOfWeek;
import pl.edu.agh.clock.view.chart.ClockChart;
import pl.edu.agh.clock.view.size.MinimumSizeManager;

public final class LabelsVisualManager {

	private static final MathChartHelper MATH_CHART_HELPER = MathChartHelper.getInstance();

	private static final String OUTER_LABEL_TEMPLATE = " %d - %d "; //$NON-NLS-1$

	private static final double MAXIMUM_TEXT_IN_RADIUS_RATIO = 0.9 * ClockChart.getSliceRadiusRatio();

	private static final FontLoader FONT_LOADER = Toolkit.getToolkit().getFontLoader();

	private static final MinimumSizeManager MINIMUM_SIZE_MANAGER = new MinimumSizeManager(new Function<Double, Dimension2D>() {
		@Override
		public Dimension2D apply(Double fontSize) {
			Font font = new Font(fontSize);

			double longestNameLength = FONT_LOADER.computeStringWidth(DayOfWeek.LONGEST_DAY_OF_WEEK_NAME, font);
			double longestNameHeight = FONT_LOADER.getFontMetrics(font).getLineHeight();

			double chartRadius = longestNameLength / MAXIMUM_TEXT_IN_RADIUS_RATIO;

			double chartWidth = 2 * chartRadius + 2 * longestNameLength;
			double chartHeight = 2 * chartRadius + 2 * longestNameHeight;

			return new Dimension2D(chartWidth, chartHeight);
		}
	});

	public void drawFrame(GraphicsContext graphicsContext, double chartRadius, double chartMiddleX, double chartMiddleY, double fontSize) {
		Font font = new Font(fontSize);
		graphicsContext.setFont(font);
		FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(font);
		double textHeight = FONT_LOADER.getFontMetrics(font).getLineHeight();

		double textY = chartMiddleY - chartRadius / 100;

		double radiusDelta = chartRadius * ClockChart.getSliceRadiusRatio();
		double currentOuterRadius = chartRadius;
		double currentInnerRadius = currentOuterRadius - radiusDelta;

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {

			String shortNameCapital = dayOfWeek.getShortNameCapital();

			double textOffestX = (radiusDelta - fontMetrics.computeStringWidth(shortNameCapital)) / 2;

			graphicsContext.strokeText(shortNameCapital, chartMiddleX - currentOuterRadius + textOffestX, textY);
			graphicsContext.fillText(shortNameCapital, chartMiddleX - currentOuterRadius + textOffestX, textY);

			graphicsContext.strokeText(shortNameCapital, chartMiddleX + currentInnerRadius + textOffestX, textY);
			graphicsContext.fillText(shortNameCapital, chartMiddleX + currentInnerRadius + textOffestX, textY);

			currentOuterRadius = currentInnerRadius;
			currentInnerRadius -= radiusDelta;
		}

		for (int hour = 0; hour < 6; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * MATH_CHART_HELPER.getOffsetCosineForHour(transformedHour);
			double outerTextY = chartMiddleY - chartRadius * MATH_CHART_HELPER.getOffsetSineForHour(transformedHour) - chartRadius / 100;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}

		for (int hour = 6; hour < 12; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * MATH_CHART_HELPER.getOffsetCosineForHour(transformedHour);
			double outerTextY = chartMiddleY - chartRadius * MATH_CHART_HELPER.getOffsetSineForHour(transformedHour) + textHeight;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}

		for (int hour = 12; hour < 18; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * MATH_CHART_HELPER.getOffsetCosineForHour(transformedHour)
					- fontMetrics.computeStringWidth(hourLabel);
			double outerTextY = chartMiddleY - chartRadius * MATH_CHART_HELPER.getOffsetSineForHour(transformedHour) + textHeight;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}

		for (int hour = 18; hour < 24; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * MATH_CHART_HELPER.getOffsetCosineForHour(transformedHour)
					- fontMetrics.computeStringWidth(hourLabel);
			double outerTextY = chartMiddleY - chartRadius * MATH_CHART_HELPER.getOffsetSineForHour(transformedHour) - chartRadius / 100;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}
	}

	public double computeNewRadius(double newWidth, double newHeight, double newFontSize) {
		FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(new Font(newFontSize));

		double chartWidth = newWidth - 2 * fontMetrics.computeStringWidth(String.format(OUTER_LABEL_TEMPLATE, 20, 20));
		double chartHeight = newHeight - 2 * fontMetrics.getLineHeight();

		return Math.max(Math.min(chartWidth, chartHeight) / 2, 0);
	}

	public Dimension2D getMinimumSize(double fontSize) {
		return MINIMUM_SIZE_MANAGER.getMinimumSize(fontSize);
	}
}
