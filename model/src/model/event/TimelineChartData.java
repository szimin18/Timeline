package model.event;

import java.util.List;

public class TimelineChartData {
	private final List<TimelineEvent> eventsList;
	private final int eventsCount;
	private final String description;

	public TimelineChartData(List<TimelineEvent> eventsList, String description) {
		this.eventsList = eventsList;
		this.description = description;
		eventsCount = eventsList.size();
	}

	public List<TimelineEvent> getEventsList() {
		return eventsList;
	}

	public int getEventsCount() {
		return eventsCount;
	}

	public String getDescription() {
		return description;
	}
}
