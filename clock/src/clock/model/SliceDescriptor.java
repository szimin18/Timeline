package clock.model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class SliceDescriptor {

	public static final List<SliceDescriptor> ALL_SLICES = Lists.newArrayList();

	private static final Map<DayOfWeek, Map<Integer, SliceDescriptor>> DESCRIPTORS = Maps.newHashMap();

	static {
		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			Map<Integer, SliceDescriptor> dayOfWeekMap = Maps.newHashMap();
			DESCRIPTORS.put(dayOfWeek, dayOfWeekMap);

			for (int hour = 0; hour < 24; hour++) {
				SliceDescriptor sliceDescriptor = new SliceDescriptor(dayOfWeek, hour);
				dayOfWeekMap.put(hour, sliceDescriptor);
				ALL_SLICES.add(sliceDescriptor);
			}
		}
	}

	private final DayOfWeek dayOfWeek;

	private final int hour;

	public static SliceDescriptor forCoordinates(DayOfWeek dayOfWeek, int hour) {
		if (dayOfWeek == null) {
			return null;
		}

		return DESCRIPTORS.get(dayOfWeek).get(hour);
	}

	private SliceDescriptor(DayOfWeek dayOfWeek, int hour) {
		this.dayOfWeek = dayOfWeek;
		this.hour = hour;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public int getHour() {
		return hour;
	}
}