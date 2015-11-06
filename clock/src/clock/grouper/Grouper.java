package clock.grouper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.dataset.TimelineDataSet;
import model.event.TimelineChartData;
import model.event.TimelineEvent;
import clock.model.DayOfWeek;
import clock.model.SliceDescriptor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public final class Grouper {
	private Grouper() {
		throw new AssertionError();
	}

	public static Map<SliceDescriptor, TimelineChartData> group(List<TimelineDataSet> timelineDataSets) {
		Multimap<SliceDescriptor, TimelineEvent> events = HashMultimap.create();

		Calendar calendarInstance = Calendar.getInstance();

		for (TimelineDataSet timelineDataSet : timelineDataSets) {
			for (TimelineEvent timelineEvent : timelineDataSet.getSortedEvents()) {

				calendarInstance.setTime(timelineEvent.getDate());

				DayOfWeek dayOfWeek = DayOfWeek.forCalendarIndex(calendarInstance.get(Calendar.DAY_OF_WEEK));
				int hour = calendarInstance.get(Calendar.HOUR_OF_DAY);

				events.put(SliceDescriptor.forCoordinates(dayOfWeek, hour), timelineEvent);
			}
		}

		Map<SliceDescriptor, TimelineChartData> result = new HashMap<>();

		for (SliceDescriptor sliceDescriptor : SliceDescriptor.ALL_SLICES) {
			DayOfWeek dayOfWeek = sliceDescriptor.getDayOfWeek();
			int hour = sliceDescriptor.getHour();

			List<TimelineEvent> eventsList = Lists.newArrayList(events.get(sliceDescriptor));
			String dataDescription = String.format("%s %2d:00-%2d:00", dayOfWeek.toString(), hour, (hour + 1) % 24);

			result.put(sliceDescriptor, new TimelineChartData(eventsList, dataDescription, null, null));
		}

		return result;
	}
}
