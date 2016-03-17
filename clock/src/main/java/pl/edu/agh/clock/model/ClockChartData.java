package pl.edu.agh.clock.model;

import java.util.Date;
import java.util.List;

public class ClockChartData {
	private final List<IClockEvent> eventsList;
	private final int eventsCount;
	private final String description;
	private final Date beginning;
	private final Date end;

	public ClockChartData(List<IClockEvent> eventsList, String description, Date beginning, Date end) {
		this.eventsList = eventsList;
		this.description = description;
		eventsCount = eventsList.size();
		this.beginning = beginning;
		this.end = end;
	}

	public List<IClockEvent> getEventsList() {
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
