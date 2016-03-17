package pl.edu.agh.clock.view.size;

import javafx.geometry.Dimension2D;
import javafx.scene.Node;

public interface ISizeManagedNode {
	Dimension2D getMinimumSize(double fontSize);

	void setSize(double newWidth, double newHeight, double newFontSize);

	Node getTopNode();
}
