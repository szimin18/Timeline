package pl.edu.agh.clock.view.size.layout;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import pl.edu.agh.clock.view.size.ISizeManagedNode;
import pl.edu.agh.clock.view.size.MinimumSizeManager;

public class HorizontalLayout extends HBox implements ISizeManagedLayout {
	private final MinimumSizeManager minimumSizeManager;

	private final List<ISizeManagedNode> sizeManagedNodes = Lists.newArrayList();

	public HorizontalLayout(final ISizeManagedNode... sizeManagedNodes) {
		for (ISizeManagedNode sizeManagedNode : sizeManagedNodes) {
			this.sizeManagedNodes.add(sizeManagedNode);
		}

		minimumSizeManager = new MinimumSizeManager(new Function<Double, Dimension2D>() {
			@Override
			public Dimension2D apply(Double fontSize) {
				double minimumWidth = 0;
				double minimumHeight = 0;

				for (ISizeManagedNode sizeManagedNode : sizeManagedNodes) {
					Dimension2D minimumSize = sizeManagedNode.getMinimumSize(fontSize);

					minimumWidth += minimumSize.getWidth();
					minimumHeight = Math.max(minimumHeight, minimumSize.getHeight());
				}

				return new Dimension2D(minimumWidth, minimumHeight);
			}
		});
	}

	@Override
	public Dimension2D getMinimumSize(double fontSize) {
		return minimumSizeManager.getMinimumSize(fontSize);
	}

	@Override
	public void setSize(double newWidth, double newHeight, double newFontSize) {
		double sumWidth = 0;
		double sumHeight = 0;

		for (ISizeManagedNode fontSizeBasedNode : sizeManagedNodes) {
			Dimension2D minimumSize = fontSizeBasedNode.getMinimumSize(newFontSize);

			sumWidth += minimumSize.getWidth();
			sumHeight = Math.max(sumHeight, minimumSize.getHeight());
		}

		setMinWidth(sumWidth);
		setMaxWidth(sumWidth);
		setMinHeight(sumHeight);
		setMaxHeight(sumHeight);

		for (ISizeManagedNode fontSizeBasedNode : sizeManagedNodes) {
			Dimension2D size = fontSizeBasedNode.getMinimumSize(newFontSize);

			fontSizeBasedNode.setSize(size.getWidth(), sumHeight, newFontSize);
		}
	}

	@Override
	public Node getTopNode() {
		return this;
	}

	@Override
	public void setActive(boolean active) {
		if (active) {
			for (ISizeManagedNode sizeManagedNode : sizeManagedNodes) {
				getChildren().add(sizeManagedNode.getTopNode());
			}
		} else {
			getChildren().clear();
		}
	}
}
