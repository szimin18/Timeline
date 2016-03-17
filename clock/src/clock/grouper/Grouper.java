package clock.grouper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import clock.model.ClockChartData;
import clock.model.DayOfWeek;
import clock.model.IClockEvent;
import clock.model.SliceDescriptor;
import clock.model.ClockDataSet;

public final class Grouper {
	private Grouper() {
		throw new AssertionError();
	}

	public static Map<SliceDescriptor, ClockChartData> group(List<ClockDataSet> clockDataSets) {
		Multimap<SliceDescriptor, IClockEvent> events = HashMultimap.create();

		Calendar calendarInstance = Calendar.getInstance();

		for (ClockDataSet clockDataSet : clockDataSets) {
			for (IClockEvent clockEvent : clockDataSet.getSortedEvents()) {

				calendarInstance.setTime(clockEvent.getDate());

				DayOfWeek dayOfWeek = DayOfWeek.forCalendarIndex(calendarInstance.get(Calendar.DAY_OF_WEEK));
				int hour = calendarInstance.get(Calendar.HOUR_OF_DAY);

				events.put(SliceDescriptor.forCoordinates(dayOfWeek, hour), clockEvent);
			}
		}

		Map<SliceDescriptor, ClockChartData> result = new HashMap<>();

		for (SliceDescriptor sliceDescriptor : SliceDescriptor.ALL_SLICES) {
			DayOfWeek dayOfWeek = sliceDescriptor.getDayOfWeek();
			int hour = sliceDescriptor.getHour();

			List<IClockEvent> eventsList = Lists.newArrayList(events.get(sliceDescriptor));
			String dataDescription = String.format("%s %2d:00-%2d:00", dayOfWeek.toString(), hour, (hour + 1) % 24);

			result.put(sliceDescriptor, new ClockChartData(eventsList, dataDescription, null, null));
		}

		return result;
	}
}
