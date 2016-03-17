package pl.edu.agh.clock.math;

import java.util.Map;

import com.google.common.collect.Maps;

public class MathChartHelper {
	private static MathChartHelper instance = null;

	private final Map<Integer, Double> sineForHour = Maps.newHashMap();

	private final Map<Integer, Double> cosineForHour = Maps.newHashMap();

	private final Map<Integer, Double> offsetSineForHour = Maps.newHashMap();

	private final Map<Integer, Double> offsetCosineForHour = Maps.newHashMap();

	private MathChartHelper() {
		double halfRadiansPerHour = Math.PI / 24.0;

		double radiansPerHour = halfRadiansPerHour * 2.0;

		for (int hour = 0; hour <= 24; hour++) {
			double radians = hour * radiansPerHour;
			double radiansWithOffset = radians + halfRadiansPerHour;

			sineForHour.put(hour, Math.sin(radians));
			cosineForHour.put(hour, Math.cos(radians));

			offsetSineForHour.put(hour, Math.sin(radiansWithOffset));
			offsetCosineForHour.put(hour, Math.cos(radiansWithOffset));
		}
	}

	public static MathChartHelper getInstance() {
		if (instance == null) {
			instance = new MathChartHelper();
		}

		return instance;
	}

	public double getSineForHour(int hour) {
		return sineForHour.get(hour);
	}

	public double getCosineForHour(int hour) {
		return cosineForHour.get(hour);
	}

	public double getOffsetSineForHour(int hour) {
		return offsetSineForHour.get(hour);
	}

	public double getOffsetCosineForHour(int hour) {
		return offsetCosineForHour.get(hour);
	}
}
