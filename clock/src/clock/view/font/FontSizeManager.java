package clock.view.font;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class FontSizeManager {
	public static final double MIN_FONT_SIZE = 1.0;

	public static final double MAX_FONT_SIZE = 30.0;

	public static final double FONT_SIZE_DELTA = 0.25;

	private final Map<IFontSizeNode, Double> currentMaximumFontSizes = Maps.newHashMap();

	private double currentlySetFontSize = 0.0;

	public void nodeResized(IFontSizeNode node, double width, double height) {
		double maximumFontSize = node.getMaximumFontSize(width, height);

		currentMaximumFontSizes.put(node, maximumFontSize);

		if (maximumFontSize != currentlySetFontSize) {
			double newFontSize = Ordering.natural().min(currentMaximumFontSizes.values());

			if (newFontSize != currentlySetFontSize) {
				currentlySetFontSize = newFontSize;

				for (IFontSizeNode fontSizeNode : currentMaximumFontSizes.keySet()) {
					fontSizeNode.setFontSize(currentlySetFontSize);
				}
			}
		}
	}
}
