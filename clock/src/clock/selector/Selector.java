package clock.selector;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import clock.util.DayOfWeek;

public class Selector extends Canvas {
	private final static double ONE_NINTH = 1.0 / 9.0;

	private final List<ICanvasListener> canvasListeners = new ArrayList<>();

	private final Node nodeForSelector;

	private final GraphicsContext graphicsContext;

	public Selector(Node nodeForSelector) {
		this.nodeForSelector = nodeForSelector;
		graphicsContext = getGraphicsContext2D();
		addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMouseMoved(event.getX(), event.getY());
			}
		});
		addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				notifyHoverRemoved();
			}
		});
		addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleMousePressed(event.getX(), event.getY());
			}
		});
		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				drawFrame();
			}
		});
		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				drawFrame();
			}
		});
		nodeForSelector.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				drawFrame();
			}
		});
	}
	
	private void drawFrame() {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());
		
		double startingX = getWidth() / 2 - getRadius();
		
		double textY = getHeight() / 2;
		
		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			graphicsContext.strokeText(dayOfWeek.getShortNameCapital(), startingX, textY);
			startingX += getRadius() * ONE_NINTH;
		}
		startingX += getRadius() * 10 * ONE_NINTH;
		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			graphicsContext.strokeText(dayOfWeek.getShortNameCapital(), startingX, textY);
			startingX -= getRadius() * ONE_NINTH;
		}
	}

	private double getRadius() {
		return nodeForSelector.boundsInLocalProperty().get().getHeight();
	}

	private void handleMousePressed(double mouseX, double mouseY) {
		double transformedX = mouseX - (widthProperty().doubleValue() / 2);
		double transformedY = mouseY - (heightProperty().doubleValue() / 2);

		transformedY = -transformedY;

		DayOfWeek dayOfWeek = getDayOfWeek(getRadius(), transformedX,
				transformedY);

		if (dayOfWeek == null) {
			notifySelectionRemoved();
		} else {
			int hour = getHour(transformedX, transformedY);

			notifySelectionChanged(dayOfWeek, hour);
		}
	}

	private void handleMouseMoved(double mouseX, double mouseY) {
		double transformedX = mouseX - (widthProperty().doubleValue() / 2);
		double transformedY = mouseY - (heightProperty().doubleValue() / 2);

		transformedY = -transformedY;

		DayOfWeek dayOfWeek = getDayOfWeek(getRadius(), transformedX,
				transformedY);

		if (dayOfWeek == null) {
			notifyHoverRemoved();
		} else {
			int hour = getHour(transformedX, transformedY);

			notifyHoverChanged(dayOfWeek, hour);
		}
	}

	private static int getHour(double transformedX, double transformedY) {
		double alpha = -Math.atan2(transformedY, transformedX) + Math.PI / 2;

		if (alpha < 0) {
			alpha += 2 * Math.PI;
		}

		return (int) (12 * alpha / Math.PI);
	}

	private static DayOfWeek getDayOfWeek(double radiusProperty, double transformedX, double transformedY) {

		double radius = Math.sqrt(transformedX * transformedX + transformedY * transformedY);

		DayOfWeek dayOfWeek = null;

		if (radius < radiusProperty) {
			if (radius >= radiusProperty * 8 * ONE_NINTH) {
				dayOfWeek = DayOfWeek.MONDAY;
			} else if (radius >= radiusProperty * 7 * ONE_NINTH) {
				dayOfWeek = DayOfWeek.TUESDAY;
			} else if (radius >= radiusProperty * 6 * ONE_NINTH) {
				dayOfWeek = DayOfWeek.WEDNESDAY;
			} else if (radius >= radiusProperty * 5 * ONE_NINTH) {
				dayOfWeek = DayOfWeek.THURSDAY;
			} else if (radius >= radiusProperty * 4 * ONE_NINTH) {
				dayOfWeek = DayOfWeek.FRIDAY;
			} else if (radius >= radiusProperty * 3 * ONE_NINTH) {
				dayOfWeek = DayOfWeek.SATURDAY;
			} else if (radius >= radiusProperty * 2 * ONE_NINTH) {
				dayOfWeek = DayOfWeek.SUNDAY;
			}
		}
		return dayOfWeek;
	}

	private void notifyHoverRemoved() {
		for (ICanvasListener listener : canvasListeners) {
			listener.hoverRemoved();
		}
	}

	private void notifyHoverChanged(DayOfWeek dayOfWeek, int hour) {
		for (ICanvasListener listener : canvasListeners) {
			listener.hoverChanged(dayOfWeek, hour);
		}
	}

	private void notifySelectionRemoved() {
		for (ICanvasListener listener : canvasListeners) {
			listener.selectionRemoved();
		}
	}

	private void notifySelectionChanged(DayOfWeek dayOfWeek, int hour) {
		for (ICanvasListener listener : canvasListeners) {
			listener.selectionChanged(dayOfWeek, hour);
		}
	}

	public void addCanvasListener(ICanvasListener listener) {
		canvasListeners.add(listener);
	}

	public void removeCanvasListener(ICanvasListener listener) {
		canvasListeners.remove(listener);
	}

	public static interface ICanvasListener {
		public void hoverRemoved();

		public void hoverChanged(DayOfWeek dayOfWeek, int hour);

		public void selectionRemoved();

		public void selectionChanged(DayOfWeek dayOfWeek, int hour);
	}
}
