package clock.view.event;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import model.event.TimelineChartData;
import model.event.TimelineEvent;
import clock.model.DayOfWeek;
import clock.model.SliceDescriptor;

public class ClockSelectionEvent {
	private final Map<SliceDescriptor, TimelineChartData> groupedData;

	private final Set<SliceDescriptor> selectedSlices;

	public ClockSelectionEvent(Map<SliceDescriptor, TimelineChartData> groupedData, Set<SliceDescriptor> selectedSlices) {
		this.groupedData = groupedData;
		this.selectedSlices = selectedSlices;
	}

	public Iterable<TimelineEvent> getSelectedEvents() {
		return new ClockEventsIterable(selectedSlices);
	}

	public boolean isSliceSelected(DayOfWeek dayOfWeek, int hour) {
		for (SliceDescriptor sliceDescriptor : selectedSlices) {
			if (dayOfWeek == sliceDescriptor.getDayOfWeek() && hour == sliceDescriptor.getHour()) {
				return true;
			}
		}
		return false;
	}

	public int getSelectedEventsCount() {
		int selectedEventsCount = 0;

		for (SliceDescriptor sliceDescriptor : selectedSlices) {
			selectedEventsCount += groupedData.get(sliceDescriptor).getEventsCount();
		}

		return selectedEventsCount;
	}

	private class ClockEventsIterable implements Iterable<TimelineEvent> {
		private Set<SliceDescriptor> selectedSlices;

		private ClockEventsIterable(Set<SliceDescriptor> selectedSlices) {
			this.selectedSlices = selectedSlices;
		}

		@Override
		public Iterator<TimelineEvent> iterator() {
			return new Iterator<TimelineEvent>() {
				private final Iterator<SliceDescriptor> outerIterator = selectedSlices.iterator();

				private Iterator<TimelineEvent> innerIterator = Collections.emptyIterator();

				@Override
				public void remove() {
				}

				@Override
				public TimelineEvent next() {
					swapIterators();

					return innerIterator.next();
				}

				@Override
				public boolean hasNext() {
					swapIterators();

					return innerIterator.hasNext();
				}

				private void swapIterators() {
					while (!innerIterator.hasNext() && outerIterator.hasNext()) {
						innerIterator = groupedData.get(outerIterator.next()).getEventsList().iterator();
					}
				}
			};
		}
	}
}
