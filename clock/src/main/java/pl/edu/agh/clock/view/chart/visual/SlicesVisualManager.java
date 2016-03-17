package pl.edu.agh.clock.view.chart.visual;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.FillRule;
import pl.edu.agh.clock.color.HeatMapColorProvider;
import pl.edu.agh.clock.grouper.QuantityLeveler.QuantityLevelProvider;
import pl.edu.agh.clock.math.MathChartHelper;
import pl.edu.agh.clock.model.ClockChartData;
import pl.edu.agh.clock.model.DayOfWeek;
import pl.edu.agh.clock.model.SliceDescriptor;
import pl.edu.agh.clock.view.chart.ClockChart;

public final class SlicesVisualManager {

	private static final MathChartHelper MATH_CHART_HELPER = MathChartHelper.getInstance();

	private static final double NORMAL_LINE_WIDTH = 1;

	private static final double SELECTED_LINE_WIDTH = 5;

	private static final double DEGREES_PER_SLICE = 360.0 / 24.0;

	private Map<SliceDescriptor, Color> colorsMap = Maps.newHashMap();

	public SlicesVisualManager(Map<SliceDescriptor, ClockChartData> groupedData, QuantityLevelProvider quantityLevelProvider) {
		for (SliceDescriptor sliceDescriptor : SliceDescriptor.ALL_SLICES) {

			int sliceEventsCount = groupedData.get(sliceDescriptor).getEventsCount();

			double sliceHeatLevel = quantityLevelProvider.getLevelForQuantity(sliceEventsCount).getLevelValue();

			colorsMap.put(sliceDescriptor, HeatMapColorProvider.getColorForValue(sliceHeatLevel));
		}
	}

	public void drawFrame(GraphicsContext graphicsContext, double chartRadius, Set<SliceDescriptor> allSelectedSlices, double chartMiddleX,
			double chartMiddleY) {

		double radiusDelta = chartRadius * ClockChart.getSliceRadiusRatio();
		double currentOuterRadius = chartRadius;
		double currentInnerRadius = currentOuterRadius - radiusDelta;

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				Color fillColor = colorsMap.get(SliceDescriptor.forCoordinates(dayOfWeek, hour));

				drawChartPart(graphicsContext, chartMiddleX, chartMiddleY, currentInnerRadius, currentOuterRadius, hour, fillColor);
			}

			currentOuterRadius = currentInnerRadius;
			currentInnerRadius -= radiusDelta;
		}

		currentOuterRadius = chartRadius;
		currentInnerRadius = currentOuterRadius - radiusDelta;

		int daysOfWeekCount = DayOfWeek.VALUES_LIST.size();

		for (int currentDayOfWeekIndex = 0; currentDayOfWeekIndex <= daysOfWeekCount; currentDayOfWeekIndex++) {
			for (int hour = 0; hour < 24; hour++) {
				int sorroundingSelectedCount = 0;

				if (currentDayOfWeekIndex != 0) {
					DayOfWeek currentDayOfWeek = DayOfWeek.VALUES_LIST.get(currentDayOfWeekIndex - 1);
					SliceDescriptor currentSlice = SliceDescriptor.forCoordinates(currentDayOfWeek, hour);
					if (allSelectedSlices.contains(currentSlice)) {
						sorroundingSelectedCount++;
					}
				}

				if (currentDayOfWeekIndex != daysOfWeekCount) {
					DayOfWeek currentDayOfWeek = DayOfWeek.VALUES_LIST.get(currentDayOfWeekIndex);
					SliceDescriptor currentSlice = SliceDescriptor.forCoordinates(currentDayOfWeek, hour);
					if (allSelectedSlices.contains(currentSlice)) {
						sorroundingSelectedCount++;
					}
				}

				double lineWidth;

				if (sorroundingSelectedCount == 1) {
					lineWidth = SELECTED_LINE_WIDTH;
				} else {
					lineWidth = NORMAL_LINE_WIDTH;
				}

				drawArc(graphicsContext, chartMiddleX, chartMiddleY, currentOuterRadius, hour, lineWidth);
			}

			currentOuterRadius = currentInnerRadius;
			currentInnerRadius -= radiusDelta;
		}

		currentOuterRadius = chartRadius;
		currentInnerRadius = currentOuterRadius - radiusDelta;

		for (int currentDayOfWeekIndex = 0; currentDayOfWeekIndex < daysOfWeekCount; currentDayOfWeekIndex++) {
			for (int hour = 0; hour < 24; hour++) {
				int sorroundingSelectedCount = 0;

				DayOfWeek currentDayOfWeek = DayOfWeek.VALUES_LIST.get(currentDayOfWeekIndex);

				int earlierHour = (hour + 23) % 24;

				SliceDescriptor earlierSlice = SliceDescriptor.forCoordinates(currentDayOfWeek, earlierHour);
				if (allSelectedSlices.contains(earlierSlice)) {
					sorroundingSelectedCount++;
				}

				SliceDescriptor currentSlice = SliceDescriptor.forCoordinates(currentDayOfWeek, hour);
				if (allSelectedSlices.contains(currentSlice)) {
					sorroundingSelectedCount++;
				}

				double lineWidth;

				if (sorroundingSelectedCount == 1) {
					lineWidth = SELECTED_LINE_WIDTH;
				} else {
					lineWidth = NORMAL_LINE_WIDTH;
				}

				drawLine(graphicsContext, chartMiddleX, chartMiddleY, currentInnerRadius, currentOuterRadius, hour - 1, lineWidth);
			}

			currentOuterRadius = currentInnerRadius;
			currentInnerRadius -= radiusDelta;
		}

		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.setLineWidth(NORMAL_LINE_WIDTH);
	}

	private void drawChartPart(GraphicsContext graphicsContext, double chartMiddleX, double chartMiddleY, double innerRadius,
			double outerRadius, int hour, Color fillColor) {
		int transformedHour = (29 - hour) % 24;

		double point1X = chartMiddleX + innerRadius * MATH_CHART_HELPER.getCosineForHour(transformedHour + 1);
		double point1Y = chartMiddleY - innerRadius * MATH_CHART_HELPER.getSineForHour(transformedHour + 1);

		double point2X = chartMiddleX + outerRadius * MATH_CHART_HELPER.getCosineForHour(transformedHour);
		double point2Y = chartMiddleY - outerRadius * MATH_CHART_HELPER.getSineForHour(transformedHour);

		graphicsContext.setFill(fillColor);
		graphicsContext.setFillRule(FillRule.NON_ZERO);
		graphicsContext.moveTo(point1X, point1Y);

		graphicsContext.beginPath();

		graphicsContext.arc(chartMiddleX, chartMiddleY, innerRadius, innerRadius, (transformedHour + 1) * DEGREES_PER_SLICE,
				-DEGREES_PER_SLICE);
		graphicsContext.lineTo(point2X, point2Y);
		graphicsContext.arc(chartMiddleX, chartMiddleY, outerRadius, outerRadius, transformedHour * DEGREES_PER_SLICE, DEGREES_PER_SLICE);
		graphicsContext.lineTo(point1X, point1Y);

		graphicsContext.closePath();
		graphicsContext.fill();
	}

	private void drawArc(GraphicsContext graphicsContext, double chartMiddleX, double chartMiddleY, double radius, int hour,
			double lineWidth) {
		int transformedHour = (29 - hour) % 24;

		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(lineWidth);

		double chartSide = 2 * radius;

		graphicsContext.strokeArc(chartMiddleX - radius, chartMiddleY - radius, chartSide, chartSide, transformedHour * DEGREES_PER_SLICE,
				DEGREES_PER_SLICE, ArcType.OPEN);
	}

	private void drawLine(GraphicsContext graphicsContext, double chartMiddleX, double chartMiddleY, double innerRadius, double outerRadius,
			int hour, double lineWidth) {
		int transformedHour = (29 - hour) % 24;

		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(lineWidth);

		double point1X = chartMiddleX + innerRadius * MATH_CHART_HELPER.getCosineForHour(transformedHour);
		double point1Y = chartMiddleY - innerRadius * MATH_CHART_HELPER.getSineForHour(transformedHour);

		double point2X = chartMiddleX + outerRadius * MATH_CHART_HELPER.getCosineForHour(transformedHour);
		double point2Y = chartMiddleY - outerRadius * MATH_CHART_HELPER.getSineForHour(transformedHour);

		graphicsContext.strokeLine(point1X, point1Y, point2X, point2Y);
	}
}
