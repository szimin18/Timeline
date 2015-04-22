package model.event;

import java.util.Collections;
import java.util.List;

public class TimelineEventsGroup {
	private final List<TimelineEvent> eventsList;
	private final int eventsCount;
	private final String groupDescription;

	private TimelineEventsGroup(List<TimelineEvent> eventsList, String groupDescription) {
		this.eventsList = Collections.unmodifiableList(eventsList);
		this.groupDescription = groupDescription;
		eventsCount = eventsList.size();
	}

	public static TimelineEventsGroup newInstance(List<TimelineEvent> eventsList, String groupDescription) {
		return new TimelineEventsGroup(eventsList, groupDescription);
	}

	public List<TimelineEvent> getEventsList() {
		return eventsList;
	}

	public int getEventsCount() {
		return eventsCount;
	}

	@Override
	public String toString() {
		return groupDescription;
	}
}
