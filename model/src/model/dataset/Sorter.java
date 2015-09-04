package model.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.event.TimelineEvent;

import com.google.common.base.Preconditions;

public class Sorter {
	private Sorter() {
		throw new AssertionError();
	}

	public static final List<TimelineEvent> sortEvents(List<TimelineEvent> events) {
		Preconditions.checkNotNull(events);

		List<TimelineEvent> sortedEvents = new ArrayList<TimelineEvent>(events);

		Collections.sort(sortedEvents, new Comparator<TimelineEvent>() {
			@Override
			public int compare(TimelineEvent o1, TimelineEvent o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

		return sortedEvents;
	}
}
