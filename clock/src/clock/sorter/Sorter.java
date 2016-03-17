package clock.sorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Preconditions;

import clock.model.IClockEvent;

public class Sorter {
	private Sorter() {
		throw new AssertionError();
	}

	public static final List<IClockEvent> sortEvents(List<IClockEvent> events) {
		Preconditions.checkNotNull(events);

		List<IClockEvent> sortedEvents = new ArrayList<IClockEvent>(events);

		Collections.sort(sortedEvents, new Comparator<IClockEvent>() {
			@Override
			public int compare(IClockEvent o1, IClockEvent o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

		return sortedEvents;
	}
}
