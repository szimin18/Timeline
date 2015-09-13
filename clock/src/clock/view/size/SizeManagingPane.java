package clock.view.size;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import clock.view.font.IFontSizeBasedNode;
import clock.view.font.MinimumSizeManager;

import com.google.common.collect.Lists;

public class SizeManagingPane extends StackPane {
	private final List<IFontSizeBasedNode> fontSizeBasedNodes = Lists.newArrayList();
	private final Pane box;

	public SizeManagingPane(IFontSizeBasedNode... fontSizeBasedNodes) {
		box = new HBox();

		getChildren().add(box);

		for (IFontSizeBasedNode fontSizeBasedNode : fontSizeBasedNodes) {
			this.fontSizeBasedNodes.add(fontSizeBasedNode);
			box.getChildren().add(fontSizeBasedNode.getNode());
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
		double fontSize = getMaximumCommonFontSize(getWidth(), getHeight());

		double sumWidth = 0;
		double sumHeight = 0;

		for (IFontSizeBasedNode fontSizeBasedNode : fontSizeBasedNodes) {
			Dimension2D minimumSize = fontSizeBasedNode.getMinimumSizeManager().getMinimumSize(fontSize);

			sumWidth += minimumSize.getWidth();
			sumHeight = Math.max(sumHeight, minimumSize.getHeight());
		}

		box.setMinWidth(sumWidth);
		box.setMaxWidth(sumWidth);
		box.setMinHeight(sumHeight);
		box.setMaxHeight(sumHeight);

		for (IFontSizeBasedNode fontSizeBasedNode : fontSizeBasedNodes) {
			Dimension2D size = fontSizeBasedNode.getMinimumSizeManager().getMinimumSize(fontSize);

			fontSizeBasedNode.setSize(size.getWidth(), sumHeight, fontSize);
		}
	}

	private double getMaximumCommonFontSize(double width, double height) {
		for (double fontSize = MinimumSizeManager.MAX_FONT_SIZE;; fontSize -= MinimumSizeManager.FONT_SIZE_DELTA) {
			if (fontSize <= MinimumSizeManager.MIN_FONT_SIZE) {
				return fontSize;
			}

			double sumWidth = 0;
			double sumHeight = 0;

			for (IFontSizeBasedNode fontSizeBasedNode : fontSizeBasedNodes) {
				Dimension2D minimumSize = fontSizeBasedNode.getMinimumSizeManager().getMinimumSize(fontSize);

				sumWidth += minimumSize.getWidth();
				sumHeight = Math.max(sumHeight, minimumSize.getHeight());
			}

			if (width >= sumWidth && height >= sumHeight) {
				return fontSize;
			}
		}
	}
}
