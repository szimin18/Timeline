package model.dataset;

import java.util.Calendar;
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

	public long getSpanInMillis() {
		Calendar begin = Calendar.getInstance();
		begin.setTime(sortedEvents.get(0).getDate());
		Calendar end = Calendar.getInstance();
		end.setTime(Iterables.getLast(sortedEvents).getDate());
		return end.getTimeInMillis() - begin.getTimeInMillis();
	}
}
