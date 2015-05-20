package model.dataset;

import java.util.List;

import javafx.scene.paint.Color;
import model.event.TimelineEvent;

import com.google.common.collect.Iterables;

public class TimelineDataSet {
	private final List<TimelineEvent> sortedEvents;
	private final Color color;

	public TimelineDataSet(List<TimelineEvent> unsortedEvents, Color color) {
		this.color = color;
		sortedEvents = Sorter.sortEvents(unsortedEvents);
	}

	public List<TimelineEvent> getSortedEvents() {
		return sortedEvents;
	}

	public Color getColor() {
		return color;
	}

	public long getTimeSpanMilliseconds() {
		if (sortedEvents.isEmpty()) {
			return 0;
		} else {
			return Iterables.getLast(sortedEvents).getDate().getTime() - sortedEvents.get(0).getDate().getTime();
		}
	}
}
