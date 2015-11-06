package clock.view.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.event.TimelineChartData;
import clock.grouper.QuantityLeveler.QuantityLevelProvider;
import clock.model.DayOfWeek;
import clock.model.SliceDescriptor;
import clock.view.chart.popup.ChartPopup;
import clock.view.chart.selection.SlicesSelectionManager;
import clock.view.chart.selection.SlicesSelectionManager.ISliceSelectionChangeStrategy;
import clock.view.chart.visual.LabelsVisualManager;
import clock.view.chart.visual.SlicesVisualManager;
import clock.view.size.ISizeManagedNode;

public class ClockChart extends Canvas implements ISizeManagedNode {
	private static final int RINGS_COUNT = 9;

	private static final double SLICE_RADIUS_RATIO = 1.0 / RINGS_COUNT;

	private final SlicesSelectionManager slicesSelectionManager = new SlicesSelectionManager(
			new ISliceSelectionChangeStrategy() {
				@Override
				public void selectionChanged(Set<SliceDescriptor> selectedSlices) {
					notifyClockChartListeners(selectedSlices);
				}
			});

	private final List<IClockChartListener> clockChartListeners = new ArrayList<>();

	private final GraphicsContext graphicsContext = getGraphicsContext2D();

	private final Map<SliceDescriptor, TimelineChartData> groupedData;

	private final ChartPopup chartPopup = new ChartPopup();

	private final SlicesVisualManager slicesVisualManager;

	private final LabelsVisualManager labelsVisualManager;

	private boolean controlKeyPressed = false;

	private double chartRadius = 0;

	private double fontSize = 0.0;

	public ClockChart(Map<SliceDescriptor, TimelineChartData> groupedData, QuantityLevelProvider quantityLevelProvider) {
		this.groupedData = groupedData;
		slicesVisualManager = new SlicesVisualManager(groupedData, quantityLevelProvider);
		labelsVisualManager = new LabelsVisualManager();

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
				SliceDescriptor sliceDescriptor = sliceDescriptorForEvent(event);

				if (sliceDescriptor == null) {
					handleMouseExited();
				} else {
					handleMouseMoved(sliceDescriptor, event.getScreenX(), event.getScreenY());
				}
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
				handleMouseDragged(sliceDescriptorForEvent(event), event.getScreenX(), event.getScreenY());
			}
		});
		addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				SliceDescriptor sliceDescriptor = sliceDescriptorForEvent(event);

				slicesSelectionManager.startSelectingSlices(sliceDescriptor, controlKeyPressed);

				handleMouseDragged(sliceDescriptor, event.getScreenX(), event.getScreenY());
			}
		});
		addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				slicesSelectionManager.finishSelectingSlices();
			}
		});
	}

	private void handleMouseExited() {
		slicesSelectionManager.cancelSelectingSlices();

		chartPopup.hide();
		drawFrame(graphicsContext);
	}

	private void handleMouseDragged(SliceDescriptor sliceDescriptor, double mouseScreenX, double mouseScreenY) {
		if (sliceDescriptor != null) {
			slicesSelectionManager.continueSelectingSlices(sliceDescriptor);

			handleMouseMoved(sliceDescriptor, mouseScreenX, mouseScreenY);
			drawFrame(graphicsContext);
		} else {
			handleMouseExited();
		}
	}

	private void handleMouseMoved(SliceDescriptor sliceDescriptor, double mouseScreenX, double mouseScreenY) {
		String description = groupedData.get(sliceDescriptor).getDescription();
		chartPopup.show(getScene().getWindow(), mouseScreenX, mouseScreenY, description);
	}

	private void drawFrame(GraphicsContext graphicsContext) {
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());

		slicesVisualManager.drawFrame(graphicsContext, chartRadius, slicesSelectionManager.getAllSelectedSlices(),
				getWidth() / 2, getHeight() / 2);
		labelsVisualManager.drawFrame(graphicsContext, chartRadius, getWidth() / 2, getHeight() / 2, fontSize);
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

		chartRadius = labelsVisualManager.computeNewRadius(newWidth, newHeight, newFontSize);

		drawFrame(graphicsContext);
	}

	@Override
	public Dimension2D getMinimumSize(double fontSize) {
		return labelsVisualManager.getMinimumSize(fontSize);
	}

	public static final double getSliceRadiusRatio() {
		return SLICE_RADIUS_RATIO;
	}

	private void notifyClockChartListeners(Set<SliceDescriptor> selectedSlices) {
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
		private final Set<SliceDescriptor> selectedSlices;

		private ClockChartSelectionEvent(Set<SliceDescriptor> selectedSlices) {
			this.selectedSlices = selectedSlices;
		}

		public Set<SliceDescriptor> getSelectedSlices() {
			return selectedSlices;
		}
	}

	private static final int hourForCoordinates(double mouseX, double mouseY) {
		double alpha = -Math.atan2(mouseY, mouseX) + Math.PI / 2;

		if (alpha < 0) {
			alpha += 2 * Math.PI;
		}

		return (int) (12 * alpha / Math.PI);
	}

	private static final DayOfWeek dayOfWeekForCoordinates(double currentChartRadius, double mouseX, double mouseY) {
		double radius = Math.sqrt(mouseX * mouseX + mouseY * mouseY);

		double radiusDelta = currentChartRadius * getSliceRadiusRatio();

		int daysOfWeekCount = DayOfWeek.VALUES_LIST.size();
		int selectedRing = -((int) (radius / radiusDelta) - RINGS_COUNT) - 1;

		if (selectedRing >= 0 && selectedRing < daysOfWeekCount) {
			return DayOfWeek.VALUES_LIST.get(selectedRing);
		} else {
			return null;
		}
	}

	private final SliceDescriptor sliceDescriptorForEvent(MouseEvent event) {
		double mouseX = event.getX() - getWidth() / 2;
		double mouseY = -(event.getY() - getHeight() / 2);

		DayOfWeek dayOfWeek = dayOfWeekForCoordinates(chartRadius, mouseX, mouseY);
		int hour = hourForCoordinates(mouseX, mouseY);

		return SliceDescriptor.forCoordinates(dayOfWeek, hour);
	}
}
