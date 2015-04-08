package model.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TimelineEventsGroup {
	private final static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

	private final List<TimelineEvent> eventsList;
	private final int eventsCount;

	private TimelineEventsGroup(List<TimelineEvent> eventsList) {
		this.eventsList = eventsList;
		eventsCount = eventsList.size();
	}

	public static TimelineEventsGroup newInstance(List<TimelineEvent> eventsList) {
		return new TimelineEventsGroup(eventsList);
	}

	public List<TimelineEvent> getEventsList() {
		return eventsList;
	}

	public int getEventsCount() {
		return eventsCount;
	}

	@Override
	public String toString() {
		List<LocalDateTime> sortedEvents = eventsList.stream().map(timelineEvent -> timelineEvent.getDateTime())
				.sorted().collect(Collectors.toList());
		return String.format("%s\n%s", formatter.format(sortedEvents.get(0)),
				formatter.format(sortedEvents.get(sortedEvents.size() - 1)));
	}
}
