package clock.view.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;
import javafx.stage.Screen;
import model.event.TimelineChartData;
import clock.color.HeatMapColorProvider;
import clock.grouper.QuantityLeveler.QuantityLevel;
import clock.grouper.QuantityLeveler.QuantityLevelProvider;
import clock.model.DayOfWeek;
import clock.view.size.ISizeManagedNode;
import clock.view.size.MinimumSizeManager;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class ClockChart extends Canvas implements ISizeManagedNode {
	private static final double SLICE_RADIUS_RATIO = 1.0 / 9.0;

	// private static final double TOP_BOTTOM_MARGIN = 13.0;
	//
	// private static final double SIDES_MARGIN = 33.0;

	private static final double CURSOR_WIDTH = 15.0;

	private static final double CURSOR_HEIGHT = 20.0;

	private static final double DEGREES_PER_SLICE = 360.0 / 24.0;

	private static final String OUTER_LABEL_TEMPLATE = " %d - %d ";

	private static final Map<Integer, Double> SINE_FOR_HOUR = Maps.newHashMap();

	private static final Map<Integer, Double> COSINE_FOR_HOUR = Maps.newHashMap();

	private static final Map<Integer, Double> OFFSET_SINE_FOR_HOUR = Maps.newHashMap();

	private static final Map<Integer, Double> OFFSET_COSINE_FOR_HOUR = Maps.newHashMap();

	static {
		double radiansPerHour = Math.PI * 2.0 / 24.0;

		for (int hour = 0; hour <= 24; hour++) {
			double radians = hour * radiansPerHour;
			double radiansWithOffset = radians + radiansPerHour / 2;

			SINE_FOR_HOUR.put(hour, Math.sin(radians));
			COSINE_FOR_HOUR.put(hour, Math.cos(radians));

			OFFSET_SINE_FOR_HOUR.put(hour, Math.sin(radiansWithOffset));
			OFFSET_COSINE_FOR_HOUR.put(hour, Math.cos(radiansWithOffset));
		}
	}

	private static final double NORMAL_LINE_WIDTH = 1;

	private static final double SELECTED_LINE_WIDTH = 5;

	private static final double MAXIMUM_TEXT_IN_RADIUS_RATIO = 0.9 * SLICE_RADIUS_RATIO;

	private static final FontLoader FONT_LOADER = Toolkit.getToolkit().getFontLoader();

	private static final String LONGEST_DAY_OF_WEEK_NAME;

	static {
		String longestDayOfWeekName = "";

		Font font = new Font(FONT_LOADER.getSystemFontSize());

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			String currentDayOfWeekName = dayOfWeek.getShortNameCapital();

			if (FONT_LOADER.computeStringWidth(currentDayOfWeekName, font) > FONT_LOADER.computeStringWidth(
					longestDayOfWeekName, font)) {
				longestDayOfWeekName = currentDayOfWeekName;
			}
		}

		LONGEST_DAY_OF_WEEK_NAME = longestDayOfWeekName;
	}

	private static final MinimumSizeManager MINIMUM_SIZE_MANAGER = new MinimumSizeManager(
			new Function<Double, Dimension2D>() {
				@Override
				public Dimension2D apply(Double fontSize) {
					Font font = new Font(fontSize);

					double longestNameLength = FONT_LOADER.computeStringWidth(LONGEST_DAY_OF_WEEK_NAME, font);
					double longestNameHeight = FONT_LOADER.getFontMetrics(font).getLineHeight();

					double chartRadius = longestNameLength / MAXIMUM_TEXT_IN_RADIUS_RATIO;

					double chartWidth = 2 * chartRadius + 2 * longestNameLength;
					double chartHeight = 2 * chartRadius + 2 * longestNameHeight;

					return new Dimension2D(chartWidth, chartHeight);
				}
			});

	private final List<IClockChartListener> clockChartListeners = new ArrayList<>();

	private final GraphicsContext graphicsContext = getGraphicsContext2D();

	private final Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData;

	private final QuantityLevelProvider quantityLevelProvider;

	private final Map<DayOfWeek, Set<Integer>> selectedElements;

	private final Map<DayOfWeek, Set<Integer>> currentlyPressedElements;

	{
		selectedElements = new HashMap<>(DayOfWeek.VALUES_LIST.size());
		currentlyPressedElements = new HashMap<>(DayOfWeek.VALUES_LIST.size());

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			selectedElements.put(dayOfWeek, new HashSet<Integer>());
			currentlyPressedElements.put(dayOfWeek, new HashSet<Integer>());
		}
	}

	private boolean controlKeyPressed = false;

	private ClockSelectionDirection currentClockPressDirection = ClockSelectionDirection.UNDEFINED;

	private Label popupLabel;

	private Popup popup;

	private DayOfWeek highlightedDayOfWeek = null;

	private int highlightedHour;

	private DayOfWeek pressedDayOfWeek = null;

	private int pressedHour;

	private double chartRadius = 0;

	private double fontSize = 0.0;

	private static enum ClockSelectionDirection {
		CLOCKWISE, COUNTERCLOCKWISE, UNDEFINED
	}

	public ClockChart(Map<DayOfWeek, Map<Integer, TimelineChartData>> groupedData,
			QuantityLevelProvider quantityLevelProvider) {
		this.groupedData = groupedData;
		this.quantityLevelProvider = quantityLevelProvider;

		popupLabel = new Label();
		popupLabel.setStyle("-fx-background-color: white;-fx-border-color: black");
		popup = PopupBuilder.create().content(popupLabel).width(200).height(50).autoFix(true).build();

		ChangeListener<Number> recalculateRadiusListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				recalculateRadius();
			}
		};

		widthProperty().addListener(recalculateRadiusListener);
		heightProperty().addListener(recalculateRadiusListener);

		setFocusTraversable(true);

		addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.CONTROL) {
					controlKeyPressed = true;
				}
			}
		});
		addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.CONTROL) {
					controlKeyPressed = false;
				}
			}
		});
		addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseMoved(event.getX() - getWidth() / 2, -(event.getY() - getHeight() / 2), event.getScreenX(),
						event.getScreenY());
			}
		});
		addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseExited();
			}
		});
		addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseDragged(event.getX() - getWidth() / 2, -(event.getY() - getHeight() / 2),
						event.getScreenX(), event.getScreenY());
			}
		});
		addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMousePressed(event.getX() - getWidth() / 2, -(event.getY() - getHeight() / 2),
						event.getScreenX(), event.getScreenY());
			}
		});
		addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseReleased();
			}
		});
	}

	private void handleMouseMoved(double mouseX, double mouseY, double mouseScreenX, double mouseScreenY) {
		DayOfWeek dayOfWeek = getDayOfWeek(mouseX, mouseY);

		if (dayOfWeek == null) {
			handleMouseExited();
		} else {
			notifyMouseMoved(mouseScreenX, mouseScreenY, dayOfWeek, getHour(mouseX, mouseY));
		}
	}

	private void handleMouseExited() {
		pressedDayOfWeek = null;
		currentClockPressDirection = ClockSelectionDirection.UNDEFINED;

		for (Set<Integer> hours : currentlyPressedElements.values()) {
			hours.clear();
		}

		notifyMouseExited();
		drawFrame();
		notifyClockChartListeners();
	}

	private void handleMouseDragged(double mouseX, double mouseY, double mouseScreenX, double mouseScreenY) {
		DayOfWeek dayOfWeek = getDayOfWeek(mouseX, mouseY);

		if (dayOfWeek == null) {
			handleMouseExited();
		} else {
			int hour = getHour(mouseX, mouseY);

			if (pressedDayOfWeek != null) {

				if (hour == pressedHour) {
					currentClockPressDirection = ClockSelectionDirection.UNDEFINED;
				} else {
					if (currentClockPressDirection == ClockSelectionDirection.UNDEFINED) {
						int hourDifferece = hour - pressedHour;

						if (hourDifferece < 0) {
							hourDifferece += 24;
						}

						if (hourDifferece <= 12) {
							currentClockPressDirection = ClockSelectionDirection.CLOCKWISE;
						} else {
							currentClockPressDirection = ClockSelectionDirection.COUNTERCLOCKWISE;
						}
					}
				}

				for (DayOfWeek dayOfWeekToClear : DayOfWeek.VALUES_LIST) {
					for (int hourToClear = 0; hourToClear < 24; hourToClear++) {
						currentlyPressedElements.get(dayOfWeekToClear).remove(hourToClear);
					}
				}

				int startingDayOfWeekIndex = pressedDayOfWeek.getIndex();
				int endingDayOfWeekIndex = dayOfWeek.getIndex();

				if (startingDayOfWeekIndex > endingDayOfWeekIndex) {
					startingDayOfWeekIndex = dayOfWeek.getIndex();
					endingDayOfWeekIndex = pressedDayOfWeek.getIndex();
				}

				int startingHour = pressedHour;
				int endingHour = hour;

				if (currentClockPressDirection == ClockSelectionDirection.COUNTERCLOCKWISE) {
					startingHour = hour;
					endingHour = pressedHour;
				}

				for (int dayOfWeekIndex = startingDayOfWeekIndex; dayOfWeekIndex <= endingDayOfWeekIndex; dayOfWeekIndex++) {
					DayOfWeek dayOfWeekToAdd = DayOfWeek.forIndex(dayOfWeekIndex);

					for (int hourToAdd = startingHour;;) {
						currentlyPressedElements.get(dayOfWeekToAdd).add(hourToAdd);

						if (hourToAdd == endingHour) {
							break;
						}

						hourToAdd = (hourToAdd + 1) % 24;
					}
				}
			}

			notifyMouseMoved(mouseScreenX, mouseScreenY, dayOfWeek, hour);
			drawFrame();
			notifyClockChartListeners();
		}
	}

	private void handleMousePressed(double mouseX, double mouseY, double mouseScreenX, double mouseScreenY) {
		DayOfWeek dayOfWeek = getDayOfWeek(mouseX, mouseY);

		if (dayOfWeek != null) {
			pressedDayOfWeek = dayOfWeek;
			pressedHour = getHour(mouseX, mouseY);
		}

		if (!controlKeyPressed) {
			for (Set<Integer> hours : selectedElements.values()) {
				hours.clear();
			}
		}

		handleMouseDragged(mouseX, mouseY, mouseScreenX, mouseScreenY);
	}

	private void handleMouseReleased() {
		pressedDayOfWeek = null;
		currentClockPressDirection = ClockSelectionDirection.UNDEFINED;
		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				if (currentlyPressedElements.get(dayOfWeek).contains(hour)) {
					selectedElements.get(dayOfWeek).add(hour);
				}
				currentlyPressedElements.get(dayOfWeek).remove(hour);
			}
		}
	}

	private void notifyMouseMoved(double mouseScreenX, double mouseScreenY, DayOfWeek dayOfWeek, int newHighlightedHour) {
		DayOfWeek oldHighlightedDayOfWeek = highlightedDayOfWeek;
		int oldHighlightedHour = highlightedHour;
		highlightedDayOfWeek = dayOfWeek;
		highlightedHour = newHighlightedHour;

		popup.show(getScene().getWindow());
		popupLabel.setText(groupedData.get(dayOfWeek).get(newHighlightedHour).getDescription());
		popup.setX(Math.min(mouseScreenX + CURSOR_WIDTH,
				Screen.getPrimary().getVisualBounds().getWidth() - popupLabel.getWidth()));
		popup.setY(mouseScreenY + CURSOR_HEIGHT);

		if (highlightedDayOfWeek != oldHighlightedDayOfWeek || highlightedHour != oldHighlightedHour) {
			drawFrame();
		}
	}

	private void notifyMouseExited() {
		DayOfWeek oldHighlightedDayOfWeek = highlightedDayOfWeek;
		int oldHighlightedHour = highlightedHour;
		highlightedDayOfWeek = null;
		highlightedHour = 0;

		popup.hide();

		if (highlightedDayOfWeek != oldHighlightedDayOfWeek || highlightedHour != oldHighlightedHour) {
			drawFrame();
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

		double radiusDelta = chartRadius * SLICE_RADIUS_RATIO;
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
		FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(new Font(fontSize));

		double width = getWidth() - 2 * fontMetrics.computeStringWidth(String.format(OUTER_LABEL_TEMPLATE, 20, 20));
		double height = getHeight() - 2 * fontMetrics.getLineHeight();

		chartRadius = Math.max(Math.min(width, height) / 2, 0);
		drawFrame();
	}

	private void drawFrame() {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		Font font = new Font(fontSize);
		graphicsContext.setFont(font);
		FontMetrics fontMetrics = FONT_LOADER.getFontMetrics(font);
		double textHeight = FONT_LOADER.getFontMetrics(font).getLineHeight();

		double chartMiddleX = getWidth() / 2;
		double chartMiddleY = getHeight() / 2;

		double textY = chartMiddleY - chartRadius / 100;

		double radiusDelta = chartRadius * SLICE_RADIUS_RATIO;
		double currentOuterRadius = chartRadius;
		double currentInnerRadius = currentOuterRadius - radiusDelta;

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				TimelineChartData timelineChartData = groupedData.get(dayOfWeek).get(hour);

				QuantityLevel levelForQuantity = quantityLevelProvider.getLevelForQuantity(timelineChartData
						.getEventsCount());

				Color fillColor = HeatMapColorProvider.getColorForValue(levelForQuantity.getLevelValue());

				drawChartPart(chartMiddleX, chartMiddleY, currentInnerRadius, currentOuterRadius, hour, fillColor);
			}

			String shortNameCapital = dayOfWeek.getShortNameCapital();

			double textOffestX = (radiusDelta - fontMetrics.computeStringWidth(shortNameCapital)) / 2;

			graphicsContext.setStroke(Color.BLACK);
			graphicsContext.setFill(Color.BLACK);

			graphicsContext.strokeText(shortNameCapital, chartMiddleX - currentOuterRadius + textOffestX, textY);
			graphicsContext.fillText(shortNameCapital, chartMiddleX - currentOuterRadius + textOffestX, textY);

			graphicsContext.strokeText(shortNameCapital, chartMiddleX + currentInnerRadius + textOffestX, textY);
			graphicsContext.fillText(shortNameCapital, chartMiddleX + currentInnerRadius + textOffestX, textY);

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
					if (selectedElements.get(currentDayOfWeek).contains(hour)
							|| currentlyPressedElements.get(currentDayOfWeek).contains(hour)) {
						sorroundingSelectedCount++;
					}
				}

				if (currentDayOfWeekIndex != daysOfWeekCount) {
					DayOfWeek currentDayOfWeek = DayOfWeek.VALUES_LIST.get(currentDayOfWeekIndex);
					if (selectedElements.get(currentDayOfWeek).contains(hour)
							|| currentlyPressedElements.get(currentDayOfWeek).contains(hour)) {
						sorroundingSelectedCount++;
					}
				}

				double lineWidth;

				if (sorroundingSelectedCount == 1) {
					lineWidth = SELECTED_LINE_WIDTH;
				} else {
					lineWidth = NORMAL_LINE_WIDTH;
				}

				drawArc(chartMiddleX, chartMiddleY, currentOuterRadius, hour, lineWidth);
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

				if (selectedElements.get(currentDayOfWeek).contains(earlierHour)
						|| currentlyPressedElements.get(currentDayOfWeek).contains(earlierHour)) {
					sorroundingSelectedCount++;
				}

				if (selectedElements.get(currentDayOfWeek).contains(hour)
						|| currentlyPressedElements.get(currentDayOfWeek).contains(hour)) {
					sorroundingSelectedCount++;
				}

				double lineWidth;

				if (sorroundingSelectedCount == 1) {
					lineWidth = SELECTED_LINE_WIDTH;
				} else {
					lineWidth = NORMAL_LINE_WIDTH;
				}

				drawLine(chartMiddleX, chartMiddleY, currentInnerRadius, currentOuterRadius, hour - 1, lineWidth);
			}

			currentOuterRadius = currentInnerRadius;
			currentInnerRadius -= radiusDelta;
		}

		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.setLineWidth(NORMAL_LINE_WIDTH);

		for (int hour = 0; hour < 6; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * getOffsetCosineForHour(transformedHour);
			double outerTextY = chartMiddleY - chartRadius * getOffsetSineForHour(transformedHour) - chartRadius / 100;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}

		for (int hour = 6; hour < 12; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * getOffsetCosineForHour(transformedHour);
			double outerTextY = chartMiddleY - chartRadius * getOffsetSineForHour(transformedHour) + textHeight;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}

		for (int hour = 12; hour < 18; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * getOffsetCosineForHour(transformedHour)
					- fontMetrics.computeStringWidth(hourLabel);
			double outerTextY = chartMiddleY - chartRadius * getOffsetSineForHour(transformedHour) + textHeight;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}

		for (int hour = 18; hour < 24; hour++) {
			String hourLabel = String.format(OUTER_LABEL_TEMPLATE, hour, hour + 1);

			int transformedHour = (29 - hour) % 24;

			double outerTextX = chartMiddleX + chartRadius * getOffsetCosineForHour(transformedHour)
					- fontMetrics.computeStringWidth(hourLabel);
			double outerTextY = chartMiddleY - chartRadius * getOffsetSineForHour(transformedHour) - chartRadius / 100;

			graphicsContext.strokeText(hourLabel, outerTextX, outerTextY);
			graphicsContext.fillText(hourLabel, outerTextX, outerTextY);
		}
	}

	private void drawChartPart(double chartMiddleX, double chartMiddleY, double innerRadius, double outerRadius,
			int hour, Color fillColor) {
		int transformedHour = (29 - hour) % 24;

		double point1X = chartMiddleX + innerRadius * getCosineForHour(transformedHour + 1);
		double point1Y = chartMiddleY - innerRadius * getSineForHour(transformedHour + 1);

		double point2X = chartMiddleX + outerRadius * getCosineForHour(transformedHour);
		double point2Y = chartMiddleY - outerRadius * getSineForHour(transformedHour);

		graphicsContext.setFill(fillColor);
		graphicsContext.setFillRule(FillRule.NON_ZERO);
		graphicsContext.moveTo(point1X, point1Y);

		graphicsContext.beginPath();

		graphicsContext.arc(chartMiddleX, chartMiddleY, innerRadius, innerRadius, (transformedHour + 1)
				* DEGREES_PER_SLICE, -DEGREES_PER_SLICE);
		graphicsContext.lineTo(point2X, point2Y);
		graphicsContext.arc(chartMiddleX, chartMiddleY, outerRadius, outerRadius, transformedHour * DEGREES_PER_SLICE,
				DEGREES_PER_SLICE);
		graphicsContext.lineTo(point1X, point1Y);

		graphicsContext.closePath();
		graphicsContext.fill();
	}

	private void drawArc(double chartMiddleX, double chartMiddleY, double radius, int hour, double lineWidth) {
		int transformedHour = (29 - hour) % 24;

		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(lineWidth);

		double chartSide = 2 * radius;

		graphicsContext.strokeArc(chartMiddleX - radius, chartMiddleY - radius, chartSide, chartSide, transformedHour
				* DEGREES_PER_SLICE, DEGREES_PER_SLICE, ArcType.OPEN);
	}

	private void drawLine(double chartMiddleX, double chartMiddleY, double innerRadius, double outerRadius, int hour,
			double lineWidth) {
		int transformedHour = (29 - hour) % 24;

		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(lineWidth);

		double point1X = chartMiddleX + innerRadius * getCosineForHour(transformedHour);
		double point1Y = chartMiddleY - innerRadius * getSineForHour(transformedHour);

		double point2X = chartMiddleX + outerRadius * getCosineForHour(transformedHour);
		double point2Y = chartMiddleY - outerRadius * getSineForHour(transformedHour);

		graphicsContext.strokeLine(point1X, point1Y, point2X, point2Y);
	}

	private static double getSineForHour(int hour) {
		return SINE_FOR_HOUR.get(hour);
	}

	private static double getCosineForHour(int hour) {
		return COSINE_FOR_HOUR.get(hour);
	}

	private static double getOffsetSineForHour(int hour) {
		return OFFSET_SINE_FOR_HOUR.get(hour);
	}

	private static double getOffsetCosineForHour(int hour) {
		return OFFSET_COSINE_FOR_HOUR.get(hour);
	}

	@Override
	public Node getTopNode() {
		return this;
	}

	@Override
	public void setSize(double newWidth, double newHeight, double newFontSize) {
		fontSize = newFontSize;

		setWidth(newWidth);
		setHeight(newHeight);

		drawFrame();
	}

	@Override
	public Dimension2D getMinimumSize(double fontSize) {
		return MINIMUM_SIZE_MANAGER.getMinimumSize(fontSize);
	}

	private void notifyClockChartListeners() {
		List<ClockChartSliceDescriptor> selectedSlices = new ArrayList<>();

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			for (int hour = 0; hour < 24; hour++) {
				if (selectedElements.get(dayOfWeek).contains(hour)
						|| currentlyPressedElements.get(dayOfWeek).contains(hour)) {
					selectedSlices.add(new ClockChartSliceDescriptor(dayOfWeek, hour));
				}
			}
		}

		ClockChartSelectionEvent event = new ClockChartSelectionEvent(selectedSlices);

		for (IClockChartListener listener : clockChartListeners) {
			listener.selectionChanged(event);
		}
	}

	public void addClockChartListener(IClockChartListener clockChartListener) {
		clockChartListeners.add(clockChartListener);
	}

	public void removeClockChartListener(IClockChartListener clockChartListener) {
		clockChartListeners.remove(clockChartListener);
	}

	public static interface IClockChartListener {
		public void selectionChanged(ClockChartSelectionEvent event);
	}

	public static final class ClockChartSelectionEvent {
		private final List<ClockChartSliceDescriptor> selectedSlices;

		private ClockChartSelectionEvent(List<ClockChartSliceDescriptor> selectedSlices) {
			this.selectedSlices = selectedSlices;
		}

		public List<ClockChartSliceDescriptor> getSelectedSlices() {
			return selectedSlices;
		}
	}

	public static final class ClockChartSliceDescriptor {
		private final DayOfWeek dayOfWeek;

		private final int hour;

		private ClockChartSliceDescriptor(DayOfWeek dayOfWeek, int hour) {
			this.dayOfWeek = dayOfWeek;
			this.hour = hour;
		}

		public DayOfWeek getDayOfWeek() {
			return dayOfWeek;
		}

		public int getHour() {
			return hour;
		}
	}
}
