package clock.graphic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.event.TimelineChartData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import clock.grouper.QuantityLeveler.QuantityLevel;
import clock.grouper.QuantityLeveler.QuantityLevelProvider;
import clock.util.DayOfWeek;

public class ClockChart extends Canvas {
	private static final double RADIUS_PART = 1.0 / 9.0;

	private static final double SIDES_MARGIN = 20.0;

	private static final double TOP_BOTTOM_MARGIN = 10.0;

	private static final double DEGREES_PER_SLICE = 360.0 / 24.0;

	private static final Map<Integer, Double> SINE_FOR_HOUR = new HashMap<>();

	private static final Map<Integer, Double> COSINE_FOR_HOUR = new HashMap<>();

	static {
		double radiansPerHour = Math.PI * 2.0 / 24.0;

		for (int hour = 0; hour <= 24; hour++) {
			double radians = hour * radiansPerHour;
			SINE_FOR_HOUR.put(hour, Math.sin(radians));
			COSINE_FOR_HOUR.put(hour, Math.cos(radians));
		}
	}

	private final GraphicsContext graphicsContext = getGraphicsContext2D();

	private final Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData;

	private final QuantityLevelProvider quantityLevelProvider;

	private final double chartBaseHue;

	private final double chartBaseSaturation;

	private final Map<DayOfWeek, Set<Integer>> selectedElements;

	{
		selectedElements = new HashMap<>(DayOfWeek.VALUES_LIST.size());

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			selectedElements.put(dayOfWeek, new HashSet<Integer>());
		}
	}

	private DayOfWeek highlightedDayOfWeek;

	private int highlightedHour;

	private double chartWidth = 0;

	private double chartHeight = 0;

	private double chartRadius = 0;

	private double chartHighlightedHue;

	private double chartHighlightedSaturation;

	private double chartSelectedHue;

	private double chartSelectedSaturation;

	public ClockChart(Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData,
			QuantityLevelProvider quantityLevelProvider, double chartBaseHue, double chartBaseSaturation,
			double chartHighlightedHue, double chartHighlightedSaturation, double chartSelectedHue,
			double chartSelectedSaturation) {
		this.groupedData = groupedData;
		this.quantityLevelProvider = quantityLevelProvider;
		this.chartBaseHue = chartBaseHue;
		this.chartBaseSaturation = chartBaseSaturation;
		this.chartHighlightedHue = chartHighlightedHue;
		this.chartHighlightedSaturation = chartHighlightedSaturation;
		this.chartSelectedHue = chartSelectedHue;
		this.chartSelectedSaturation = chartSelectedSaturation;

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				chartWidth = newValue.doubleValue() - 2 * SIDES_MARGIN;
				recalculateRadius();
			}
		});
		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				chartHeight = newValue.doubleValue() - 2 * TOP_BOTTOM_MARGIN;
				recalculateRadius();
			}
		});

		addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseMoved(event.getX() - getWidth() / 2, -(event.getY() - getHeight() / 2));
			}
		});
		addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				notifyMouseExited();
			}
		});
		addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseClicked(event.getX() - getWidth() / 2, -(event.getY() - getHeight() / 2));
			}
		});
	}

	private void handleMouseMoved(double mouseX, double mouseY) {
		DayOfWeek dayOfWeek = getDayOfWeek(mouseX, mouseY);

		if (dayOfWeek == null) {
			notifyMouseExited();
		} else {
			DayOfWeek oldHighlightedDayOfWeek = highlightedDayOfWeek;
			int oldHighlightedHour = highlightedHour;
			highlightedDayOfWeek = dayOfWeek;
			highlightedHour = getHour(mouseX, mouseY);
			if (highlightedDayOfWeek != oldHighlightedDayOfWeek || highlightedHour != oldHighlightedHour) {
				redrawChart();
			}
		}
	}

	private void notifyMouseExited() {
		DayOfWeek oldHighlightedDayOfWeek = highlightedDayOfWeek;
		int oldHighlightedHour = highlightedHour;
		highlightedDayOfWeek = null;
		highlightedHour = 0;
		if (highlightedDayOfWeek != oldHighlightedDayOfWeek || highlightedHour != oldHighlightedHour) {
			redrawChart();
		}
	}

	private void handleMouseClicked(double mouseX, double mouseY) {
		DayOfWeek dayOfWeek = getDayOfWeek(mouseX, mouseY);

		if (dayOfWeek != null) {
			int hour = getHour(mouseX, mouseY);

			if (selectedElements.get(dayOfWeek).contains(hour)) {
				selectedElements.get(dayOfWeek).remove(hour);
			} else {
				selectedElements.get(dayOfWeek).add(hour);
			}

			redrawChart();
		}
	}

	private static int getHour(double mouseX, double mouseY) {
		double alpha = -Math.atan2(mouseY, mouseX) + Math.PI / 2;

		if (alpha < 0) {
			alpha += 2 * Math.PI;
		}

		return (int) (12 * alpha / Math.PI);
	}

	private DayOfWeek getDayOfWeek(double mouseX, double mouseY) {

		double radius = Math.sqrt(mouseX * mouseX + mouseY * mouseY);

		double radiusDelta = chartRadius * RADIUS_PART;
		double currentOuterRadius = chartRadius;
		double currentInnerRadius = currentOuterRadius - radiusDelta;

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			if (currentInnerRadius < radius && radius <= currentOuterRadius) {
				return dayOfWeek;
			}

			currentOuterRadius = currentInnerRadius;
			currentInnerRadius -= radiusDelta;
		}

		return null;
	}

	private void recalculateRadius() {
		double oldValue = chartRadius;
		chartRadius = Math.min(chartWidth, chartHeight);
		chartRadius /= 2.0;
		if (oldValue != chartRadius) {
			redrawChart();
		}
	}

	private void redrawChart() {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		double chartMiddleX = getWidth() / 2;
		double chartMiddleY = getHeight() / 2;

		double radiusDelta = chartRadius * RADIUS_PART;
		double currentOuterRadius = chartRadius;
		double currentInnerRadius = currentOuterRadius - radiusDelta;

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				TimelineChartData timelineChartData = groupedData.get(dayOfWeek).get(hour);

				QuantityLevel levelForQuantity = quantityLevelProvider.getLevelForQuantity(timelineChartData
						.getEventsCount());

				double sliceBrightness = levelForQuantity.getLevelValue();

				double sliceHue;
				double sliceSaturation;

				if (highlightedDayOfWeek == dayOfWeek && highlightedHour == hour) {
					sliceHue = chartHighlightedHue;
					sliceSaturation = chartHighlightedSaturation;
				} else if (selectedElements.get(dayOfWeek).contains(hour)) {
					sliceHue = chartSelectedHue;
					sliceSaturation = chartSelectedSaturation;
				} else {
					sliceHue = chartBaseHue;
					sliceSaturation = chartBaseSaturation;
				}

				Color fillColor = Color.hsb(sliceHue, sliceSaturation, sliceBrightness);
				Color strokeColor = Color.hsb(sliceHue, sliceSaturation, sliceBrightness / 2);

				drawChartPart(chartMiddleX, chartMiddleY, currentInnerRadius, currentOuterRadius, hour, strokeColor,
						fillColor);
			}

			currentOuterRadius = currentInnerRadius;
			currentInnerRadius -= radiusDelta;
		}
	}

	private void drawChartPart(double chartMiddleX, double chartMiddleY, double innerRadius, double outerRadius,
			int hour, Color strokeColor, Color fillColor) {
		hour = (29 - hour) % 24;

		double point1X = chartMiddleX + innerRadius * getCosineForHour(hour + 1);
		double point1Y = chartMiddleY - innerRadius * getSineForHour(hour + 1);

		double point2X = chartMiddleX + outerRadius * getCosineForHour(hour);
		double point2Y = chartMiddleY - outerRadius * getSineForHour(hour);

		graphicsContext.setFill(fillColor);
		graphicsContext.setStroke(strokeColor);
		graphicsContext.setFillRule(FillRule.NON_ZERO);
		graphicsContext.moveTo(point1X, point1Y);

		graphicsContext.beginPath();

		graphicsContext.arc(chartMiddleX, chartMiddleY, innerRadius, innerRadius, (hour + 1) * DEGREES_PER_SLICE,
				-DEGREES_PER_SLICE);
		graphicsContext.lineTo(point2X, point2Y);
		graphicsContext.arc(chartMiddleX, chartMiddleY, outerRadius, outerRadius, hour * DEGREES_PER_SLICE,
				DEGREES_PER_SLICE);
		graphicsContext.lineTo(point1X, point1Y);

		graphicsContext.closePath();
		graphicsContext.fill();
		graphicsContext.stroke();
	}

	private static double getSineForHour(int hour) {
		return SINE_FOR_HOUR.get(hour);
	}

	private static double getCosineForHour(int hour) {
		return COSINE_FOR_HOUR.get(hour);
	}

	public static interface IClockChartListener {
		public void hoverChanged(DayOfWeek dayOfWeek, int hour);

		public void selectionRemoved();

		public void selectionChanged(DayOfWeek dayOfWeek, int hour);
	}
}
