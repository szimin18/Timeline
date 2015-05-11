package histogram.selector;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Selector extends Canvas {
	private final static double HOLDER_INTER_LINES_GAP = 4;

	private final static double HOLDER_VERTICAL_GAPS = 4;

	private final static Color GRAYED_COLOR = Color.gray(0.5, 0.5);

	private enum DraggedPart {
		LEFT, ALL, RIGHT, NONE
	}

	private GraphicsContext graphicsContext;

	private double topY;
	private double bottomY;
	private double chartHeight;
	private double leftX;
	private double chartWidth;

	private double pressX;
	private DraggedPart draggedPart;

	private Bounds leftHolderBounds = new BoundingBox(0, 0, 0, 0);
	private Bounds rightHolderBounds = new BoundingBox(0, 0, 0, 0);
	private Bounds bottomHolderBounds = new BoundingBox(0, 0, 0, 0);

	private int currentLeftTickIndex = 0;
	private int currentRightTickIndex = 0;

	private final List<TimelineTick> timelineTicks = new ArrayList<>();

	public Selector() {
		graphicsContext = this.getGraphicsContext2D();
		graphicsContext.setLineWidth(2);
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setFill(GRAYED_COLOR);

		addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mousePressed(event);
			}
		});
		addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseDragged(event);
			}
		});
		addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseReleased(event);
			}
		});
		addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Point2D clickPoint = new Point2D(event.getX(), event.getY());
				if (leftHolderBounds.contains(clickPoint) || rightHolderBounds.contains(clickPoint) || bottomHolderBounds.contains(clickPoint)) {
					setCursor(Cursor.CLOSED_HAND);
				} else {
					setCursor(Cursor.DEFAULT);
				}
			}
		});

		boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> arg0, Bounds arg1, Bounds arg2) {
				drawFrame();
			}
		});
	}

	public void setLeftX(double leftX) {
		this.leftX = leftX;
		drawFrame();
	}

	public void setChartWidth(double chartWidth) {
		this.chartWidth = chartWidth;
		drawFrame();
	}

	public void setTopY(double topY) {
		this.topY = topY;
		bottomY = topY + chartHeight;
		drawFrame();
	}

	public void setChartHeight(double chartHeight) {
		this.chartHeight = chartHeight;
		bottomY = topY + chartHeight;
		drawFrame();
	}

	public void drawFrame() {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		if (!timelineTicks.isEmpty()) {
			double xLeft = timelineTicks.get(currentLeftTickIndex).getLeft();
			double xRight = timelineTicks.get(currentRightTickIndex).getRight();

			graphicsContext.strokeLine(xLeft, topY, xLeft, bottomY);
			graphicsContext.strokeLine(xRight, topY, xRight, bottomY);

			leftHolderBounds = new BoundingBox(xLeft - 2 * HOLDER_INTER_LINES_GAP, topY - 3 * HOLDER_INTER_LINES_GAP,
					4 * HOLDER_INTER_LINES_GAP, 3 * HOLDER_INTER_LINES_GAP);

			rightHolderBounds = new BoundingBox(xRight - 2 * HOLDER_INTER_LINES_GAP, topY - 3 * HOLDER_INTER_LINES_GAP,
					4 * HOLDER_INTER_LINES_GAP, 3 * HOLDER_INTER_LINES_GAP);

			bottomHolderBounds = new BoundingBox(xLeft, bottomY, xRight - xLeft, 3 * HOLDER_INTER_LINES_GAP);

			drawHolder(leftHolderBounds);
			drawHolder(rightHolderBounds);
			drawHolder(bottomHolderBounds);

			graphicsContext.fillRect(leftX, topY, xLeft - leftX, chartHeight);
			graphicsContext.fillRect(xRight, topY, leftX + chartWidth - xRight, chartHeight);
		}
	}

	private void drawHolder(Bounds bounds) {
		graphicsContext.strokeRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

		double middle = (bounds.getMinX() + bounds.getMaxX()) / 2;
		double left = middle - HOLDER_INTER_LINES_GAP;
		double right = middle + HOLDER_INTER_LINES_GAP;
		double linesTop = bounds.getMinY() + HOLDER_VERTICAL_GAPS;
		double linesBottom = bounds.getMaxY() - HOLDER_VERTICAL_GAPS;

		graphicsContext.strokeLine(left, linesTop, left, linesBottom);
		graphicsContext.strokeLine(middle, linesTop, middle, linesBottom);
		graphicsContext.strokeLine(right, linesTop, right, linesBottom);
	}

	private void mousePressed(MouseEvent e) {
		if (!timelineTicks.isEmpty()) {
			double mouseX = e.getX();
			double mouseY = e.getY();

			if (leftHolderBounds.contains(mouseX, mouseY)) {
				draggedPart = DraggedPart.LEFT;
			} else if (rightHolderBounds.contains(mouseX, mouseY)) {
				draggedPart = DraggedPart.RIGHT;
			} else if (bottomHolderBounds.contains(mouseX, mouseY)) {
				draggedPart = DraggedPart.ALL;
			}

			pressX = mouseX;
		}
	}

	private void mouseDragged(MouseEvent e) {
		if (draggedPart != DraggedPart.NONE) {
			double x = e.getX();
			double diff = x - pressX;

			if (draggedPart == DraggedPart.LEFT) {
				double oldLeft = timelineTicks.get(currentLeftTickIndex).getLeft();
				currentLeftTickIndex = findIndexForLeft(oldLeft + diff);
				double newLeft = timelineTicks.get(currentLeftTickIndex).getLeft();
				pressX += newLeft - oldLeft;
				drawFrame();
			} else if (draggedPart == DraggedPart.RIGHT) {
				double oldRight = timelineTicks.get(currentRightTickIndex).getRight();
				currentRightTickIndex = findIndexForRight(oldRight + diff);
				double newRight = timelineTicks.get(currentRightTickIndex).getRight();
				pressX += newRight - oldRight;
				drawFrame();
			} else if (draggedPart == DraggedPart.ALL) {
				double oldLeft = timelineTicks.get(currentLeftTickIndex).getLeft();
				int shiftForAll = findShiftForAll(oldLeft + diff);
				currentLeftTickIndex += shiftForAll;
				currentRightTickIndex += shiftForAll;
				double newLeft = timelineTicks.get(currentLeftTickIndex).getLeft();
				pressX += newLeft - oldLeft;
				drawFrame();
			}
		}
	}

	private int findIndexForLeft(double x) {
		double bestFit = timelineTicks.get(timelineTicks.size() - 1).getRight() - timelineTicks.get(0).getLeft();
		int bestFitIndex = currentLeftTickIndex;

		for (int i = 0; i < timelineTicks.size(); i++) {
			double tickFit = Math.abs(timelineTicks.get(i).getLeft() - x);
			if (tickFit < bestFit) {
				bestFit = tickFit;
				bestFitIndex = i;
			}
		}

		return Math.min(bestFitIndex, currentRightTickIndex);
	}

	private int findIndexForRight(double x) {
		double bestFit = timelineTicks.get(timelineTicks.size() - 1).getRight() - timelineTicks.get(0).getLeft();
		int bestFitIndex = currentRightTickIndex;

		for (int i = 0; i < timelineTicks.size(); i++) {
			double tickFit = Math.abs(timelineTicks.get(i).getRight() - x);
			if (tickFit < bestFit) {
				bestFit = tickFit;
				bestFitIndex = i;
			}
		}

		return Math.max(bestFitIndex, currentLeftTickIndex);
	}

	private int findShiftForAll(double x) {
		double bestFit = timelineTicks.get(timelineTicks.size() - 1).getRight() - timelineTicks.get(0).getLeft();
		int bestFitIndex = currentLeftTickIndex;

		for (int i = 0; i < timelineTicks.size(); i++) {
			double tickFit = Math.abs(timelineTicks.get(i).getLeft() - x);
			if (tickFit < bestFit) {
				bestFit = tickFit;
				bestFitIndex = i;
			}
		}

		return Math.min(bestFitIndex - currentLeftTickIndex, timelineTicks.size() - currentRightTickIndex - 1);
	}

	private void mouseReleased(MouseEvent e) {
		draggedPart = DraggedPart.NONE;
	}

	public TimelineTick newTimelineTick() {
		TimelineTick timelineTick = new TimelineTick();
		timelineTicks.add(timelineTick);
		currentRightTickIndex = timelineTicks.size() - 1;
		return timelineTick;
	}

	public static final class TimelineTick {
		private double left = 0;
		private double width = 0;

		private TimelineTick() {
		}

		public double getLeft() {
			return left;
		}

		public double getRight() {
			return left + width;
		}

		public void setLeft(double left) {
			this.left = left;
		}

		public void setWidth(double width) {
			this.width = width;
		}
	}
}
