package clock.grouper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.dataset.TimelineDataSet;
import model.event.TimelineChartData;
import model.event.TimelineEvent;
import clock.model.DayOfWeek;

public final class Grouper {
	private Grouper() {
		throw new AssertionError();
	}

	public static Map<DayOfWeek, Map<Integer, TimelineChartData>> group(List<TimelineDataSet> timelineDataSets) {
		Map<DayOfWeek, Map<Integer, List<TimelineEvent>>> eventsMap = new HashMap<>();

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			Map<Integer, List<TimelineEvent>> dayOfWeekMap = new HashMap<>();
			for (int hour = 0; hour < 24; hour++) {
				dayOfWeekMap.put(hour, new ArrayList<TimelineEvent>());
			}
			eventsMap.put(dayOfWeek, dayOfWeekMap);
		}

		Calendar calendarInstance = Calendar.getInstance();

		for (TimelineDataSet timelineDataSet : timelineDataSets) {
			for (TimelineEvent timelineEvent : timelineDataSet.getSortedEvents()) {
				calendarInstance.setTime(timelineEvent.getDate());
				eventsMap.get(DayOfWeek.forCalendarIndex(calendarInstance.get(Calendar.DAY_OF_WEEK)))
						.get(calendarInstance.get(Calendar.HOUR_OF_DAY)).add(timelineEvent);
			}
		}

		Map<DayOfWeek, Map<Integer, TimelineChartData>> result = new HashMap<>();

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			Map<Integer, TimelineChartData> dayOfWeekMap = new HashMap<>();
			for (int hour = 0; hour < 24; hour++) {
				List<TimelineEvent> eventsList = eventsMap.get(dayOfWeek).get(hour);
				String dataDescription = String.format("%s %2d:00-%2d:00", dayOfWeek.toString(), hour, (hour + 1) % 24);
				TimelineChartData timelineChartData = new TimelineChartData(eventsList, dataDescription, null, null);
				dayOfWeekMap.put(hour, timelineChartData);
			}
			result.put(dayOfWeek, dayOfWeekMap);
		}

		return result;
	}
}
