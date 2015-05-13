package model.event;

import java.util.Date;
import java.util.List;

public class TimelineChartData {
	private final List<TimelineEvent> eventsList;
	private final int eventsCount;
	private final String description;
	private final Date beginning;
	private final Date end;

	public TimelineChartData(List<TimelineEvent> eventsList, String description, Date beginning, Date end) {
		this.eventsList = eventsList;
		this.description = description;
		eventsCount = eventsList.size();
		this.beginning = beginning;
		this.end = end;
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

	public Date getBeginning() {
		return beginning;
	}

	public Date getEnd() {
		return end;
	}
}
