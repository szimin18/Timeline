package clock.view.font;

import javafx.scene.Node;

public interface IFontSizeBasedNode {
	public MinimumSizeManager getMinimumSizeManager();

	public void setSize(double newWidth, double newHeight, double newFontSize);
	
	public Node getNode();
}
