package pl.edu.agh.clock.view.size;

import java.util.List;

import com.google.common.collect.Lists;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.StackPane;
import pl.edu.agh.clock.view.size.layout.ISizeManagedLayout;

public class SizeManagingPane extends StackPane {
	private final List<ISizeManagedLayout> alternativeLayouts = Lists.newArrayList();

	private ISizeManagedLayout activeLayout = null;

	public SizeManagingPane(ISizeManagedLayout... alternativeLayouts) {
		for (ISizeManagedLayout alternativeLayout : alternativeLayouts) {
			this.alternativeLayouts.add(alternativeLayout);
		}

		ChangeListener<Number> sizeChangeListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				drawFrame();
			}
		};

		widthProperty().addListener(sizeChangeListener);
		heightProperty().addListener(sizeChangeListener);
	}

	private void drawFrame() {
		double width = getWidth();
		double height = getHeight();

		double biggestFontSize = MinimumSizeManager.MIN_FONT_SIZE;
		ISizeManagedLayout biggestFontSizeLayout = null;

		for (ISizeManagedLayout layout : alternativeLayouts) {
			double fontSize = getMaximumFontSize(layout, width, height);

			if (fontSize > biggestFontSize) {
				biggestFontSize = fontSize;
				biggestFontSizeLayout = layout;
			}
		}

		if (activeLayout != null) {
			activeLayout.setActive(false);
		}

		getChildren().clear();

		if (biggestFontSizeLayout == null) {
			biggestFontSizeLayout = alternativeLayouts.get(0);
		}

		activeLayout = biggestFontSizeLayout;
		activeLayout.setActive(true);
		getChildren().add(activeLayout.getTopNode());

		Dimension2D size = activeLayout.getMinimumSize(biggestFontSize);
		activeLayout.setSize(size.getWidth(), size.getHeight(), biggestFontSize);
	}

	private double getMaximumFontSize(ISizeManagedLayout alternativeNode, double width, double height) {
		for (double fontSize = MinimumSizeManager.MAX_FONT_SIZE;; fontSize -= MinimumSizeManager.FONT_SIZE_DELTA) {
			if (fontSize <= MinimumSizeManager.MIN_FONT_SIZE) {
				return fontSize;
			}

			Dimension2D minimumSize = alternativeNode.getMinimumSize(fontSize);

			if (width >= minimumSize.getWidth() && height >= minimumSize.getHeight()) {
				return fontSize;
			}
		}
	}
}
