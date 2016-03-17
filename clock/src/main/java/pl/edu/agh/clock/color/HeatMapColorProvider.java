package pl.edu.agh.clock.color;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

public class HeatMapColorProvider {
	private static final Map<Double, Color> COLORS_MAP = new HashMap<>();

	static {
		COLORS_MAP.put(0.0, Color.rgb(255, 219, 37));
		COLORS_MAP.put(0.25, Color.rgb(232, 178, 34));
		COLORS_MAP.put(0.5, Color.rgb(255, 177, 50));
		COLORS_MAP.put(0.75, Color.rgb(232, 130, 34));
		COLORS_MAP.put(1.0, Color.rgb(255, 106, 34));
	}

	private HeatMapColorProvider() {
		throw new AssertionError();
	}

	/**
	 * Calculates color for given value.
	 * 
	 * @param value
	 *            in range [0, 1]
	 * @return calculated color
	 */
	public static Color getColorForValue(double value) {
		double valueUnder = 0.0;
		double valueAbove = 1.0;

		for (double currentValue : COLORS_MAP.keySet()) {
			if (currentValue < valueAbove && currentValue >= value) {
				valueAbove = currentValue;
			}
			if (currentValue > valueUnder && currentValue <= value) {
				valueUnder = currentValue;
			}
		}

		Color underColor = COLORS_MAP.get(valueUnder);
		Color aboveColor = COLORS_MAP.get(valueAbove);

		double underPart;
		double abovePart;
		if (valueAbove == valueUnder) {
			underPart = 0.5;
			abovePart = 0.5;
		} else {
			underPart = (valueAbove - value) / (valueAbove - valueUnder);
			abovePart = (value - valueUnder) / (valueAbove - valueUnder);
		}

		double interpolatedHue = underPart * underColor.getHue() + abovePart * aboveColor.getHue();
		double interpolatedSaturation = underPart * underColor.getSaturation() + abovePart * aboveColor.getSaturation();
		double interpolatedBrightness = underPart * underColor.getBrightness() + abovePart * aboveColor.getBrightness();

		return Color.hsb(interpolatedHue, interpolatedSaturation, interpolatedBrightness);
	}
}
