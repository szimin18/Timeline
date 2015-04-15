package presenter.selector;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Selector extends Canvas {

	private enum Part {
		BEGINNIGN, ALL, END
	}

	private GraphicsContext graphicsContext;

	private int hSize;
	private int spacing;

	private int previousDrag;
	private Part draggedPart;
	private boolean dragging = false;

	private int currentBeg = 0;
	private int currentEnd = 100;

	public Selector(int wSize, int hSize, int spacing) {
		super(wSize, hSize);
		this.hSize = hSize;
		this.spacing = spacing;
		graphicsContext = this.getGraphicsContext2D();
		drawFrame(100, 200);
		addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> mousePressed(e));
		addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> mouseDragged(e));
		addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> mouseReleased(e));
	}

	public void drawFrame(int x1, int x2) {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.strokePolyline(
				new double[] { x1, x1 + 13, x1 + 13, x1 }, new double[] {
						spacing, spacing + 13, hSize - 13, hSize }, 4);
		graphicsContext.strokePolyline(
				new double[] { x2, x2 - 13, x2 - 13, x2 }, new double[] {
						spacing, spacing + 13, hSize - 13, hSize }, 4);
		graphicsContext.setFill(Color.rgb(0, 255, 255, 0.4));
		graphicsContext.fillPolygon(
				new double[] { x1, x1 + 13, x2 - 13, x2 }, new double[] {
						hSize, hSize - 13, hSize - 13, hSize }, 4);
		graphicsContext.setFill(Color.rgb(0, 0, 255, 0.2));
		graphicsContext.fillPolygon(
				new double[] { x1, x1 + 13, x1 + 13, x1 }, new double[] {
						spacing, spacing + 13, hSize - 13, hSize }, 4);
		graphicsContext.fillPolygon(
				new double[] { x2, x2 - 13, x2 - 13, x2 }, new double[] {
						spacing, spacing + 13, hSize - 13, hSize }, 4);
		currentBeg = x1;
		currentEnd = x2;
	}

	private void mousePressed(MouseEvent e) {
		int x = (int) e.getX();
		int y = (int) e.getY();
		if (x > currentBeg && currentBeg + 13 > x) {
			draggedPart = Part.BEGINNIGN;
			dragging = true;
		} else if (x > currentEnd - 13 && currentEnd > x) {
			draggedPart = Part.END;
			dragging = true;
		} else if (x > currentBeg + 13 && currentEnd - 13 > x && y > hSize - 13) {
			draggedPart = Part.ALL;
			dragging = true;
		}
		previousDrag = x;
	}

	private void mouseDragged(MouseEvent e) {
		if (dragging) {
			int x = (int) e.getX();
			int diff = x - previousDrag;
			previousDrag = x;
			if (draggedPart == Part.BEGINNIGN)
				drawFrame(currentBeg + diff, currentEnd);
			else if (draggedPart == Part.END)
				drawFrame(currentBeg, currentEnd + diff);
			else
				drawFrame(currentBeg + diff, currentEnd + diff);
		}
	}

	private void mouseReleased(MouseEvent e) {
		dragging = false;
	}

}
