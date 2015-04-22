package histogram.selector;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Selector extends Canvas {
	private static final Color COLOR1 = Color.rgb(0, 255, 255, 0.4);
	private static final Color COLOR2 = Color.rgb(0, 0, 255, 0.2);
	private static final int EDGE_CONSTANT = 13;

	private enum DraggedPart {
		LEFT, ALL, RIGHT, NONE
	}

	private GraphicsContext graphicsContext;

	private double topY;
	private double chartHeight;

	private double pressX;
	private DraggedPart draggedPart;

	private int currentLeftTickIndex = 0;
	private int currentRightTickIndex = 0;

	private final List<TimelineTick> timelineTicks = new ArrayList<>();

	public Selector() {
		graphicsContext = this.getGraphicsContext2D();
		addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
		addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
		addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
		boundsInLocalProperty().addListener((observable, oldValue, newValue) -> drawFrame());
	}

	public void setTopY(double topY) {
		this.topY = topY;
		drawFrame();
	}

	private double getTopY() {
		return topY;
	}

	public void setChartHeight(double chartHeight) {
		this.chartHeight = chartHeight;
		drawFrame();
	}

	private double getBottomY() {
		return topY + chartHeight;
	}

	public void drawFrame() {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		double xLeft = 0;
		double xRight = 0;

		if (timelineTicks.size() > 0) {
			xLeft = timelineTicks.get(currentLeftTickIndex).getLeft();
			xRight = timelineTicks.get(currentRightTickIndex).getRight();
		}

		double[] leftXLocations = new double[] { xLeft - EDGE_CONSTANT, xLeft, xLeft, xLeft - EDGE_CONSTANT };

		double[] rightXLocations = new double[] { xRight + EDGE_CONSTANT, xRight, xRight, xRight + EDGE_CONSTANT };

		double[] sideYLocations = { getTopY() - EDGE_CONSTANT, getTopY(), getBottomY(), getBottomY() + EDGE_CONSTANT };

		double[] bottomXLocations = new double[] { xLeft - EDGE_CONSTANT, xLeft, xRight, xRight + EDGE_CONSTANT };

		double[] bottomYLocations = new double[] { getBottomY() + EDGE_CONSTANT, getBottomY(), getBottomY(),
				getBottomY() + EDGE_CONSTANT };

		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.strokePolyline(leftXLocations, sideYLocations, 4);
		graphicsContext.strokePolyline(rightXLocations, sideYLocations, 4);

		graphicsContext.setFill(COLOR2);
		graphicsContext.fillPolygon(leftXLocations, sideYLocations, 4);
		graphicsContext.fillPolygon(rightXLocations, sideYLocations, 4);

		graphicsContext.setFill(COLOR1);
		graphicsContext.fillPolygon(bottomXLocations, bottomYLocations, 4);
	}

	private void mousePressed(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();

		double leftValue = timelineTicks.get(currentLeftTickIndex).getLeft();
		double rightValue = timelineTicks.get(currentRightTickIndex).getRight();

		if (leftValue - EDGE_CONSTANT <= mouseX && mouseX <= leftValue && getTopY() - EDGE_CONSTANT <= mouseY
				&& mouseY <= getBottomY() + EDGE_CONSTANT) {
			draggedPart = DraggedPart.LEFT;
		} else if (rightValue <= mouseX && mouseX <= rightValue + EDGE_CONSTANT && getTopY() - EDGE_CONSTANT <= mouseY
				&& mouseY <= getBottomY() + EDGE_CONSTANT) {
			draggedPart = DraggedPart.RIGHT;
		} else if (leftValue <= mouseX && mouseX <= rightValue && getBottomY() <= mouseY
				&& mouseY <= getBottomY() + EDGE_CONSTANT) {
			draggedPart = DraggedPart.ALL;
		}
		pressX = mouseX;
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
