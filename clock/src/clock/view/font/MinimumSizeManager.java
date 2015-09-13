package clock.view.font;

import java.util.Map;

import javafx.geometry.Dimension2D;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class MinimumSizeManager {
	public static final double MIN_FONT_SIZE = 1.0;

	public static final double MAX_FONT_SIZE = 30.0;

	public static final double FONT_SIZE_DELTA = 0.125;

	private final Map<Double, Dimension2D> minimumChartSizes = Maps.newHashMap();

	public MinimumSizeManager(Function<Double, Dimension2D> minimumSizeProvider) {
		for (double fontSize = MIN_FONT_SIZE; fontSize <= MAX_FONT_SIZE; fontSize += FONT_SIZE_DELTA) {
			minimumChartSizes.put(fontSize, minimumSizeProvider.apply(fontSize));
		}
	}

	public Dimension2D getMinimumSize(double fontSize) {
		double currentFontSize = ((int) (fontSize / FONT_SIZE_DELTA)) * FONT_SIZE_DELTA;

		if (currentFontSize < fontSize) {
			currentFontSize += FONT_SIZE_DELTA;
		}
		
		return minimumChartSizes.get(Math.min(Math.max(currentFontSize, MIN_FONT_SIZE), MAX_FONT_SIZE));
	}
}
