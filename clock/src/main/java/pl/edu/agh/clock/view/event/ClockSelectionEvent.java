package pl.edu.agh.clock.view.event;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pl.edu.agh.clock.model.ClockChartData;
import pl.edu.agh.clock.model.DayOfWeek;
import pl.edu.agh.clock.model.IClockEvent;
import pl.edu.agh.clock.model.SliceDescriptor;

public class ClockSelectionEvent {
	private final Map<SliceDescriptor, ClockChartData> groupedData;

	private final Set<SliceDescriptor> selectedSlices;

	public ClockSelectionEvent(Map<SliceDescriptor, ClockChartData> groupedData, Set<SliceDescriptor> selectedSlices) {
		this.groupedData = groupedData;
		this.selectedSlices = selectedSlices;
	}

	public Iterable<IClockEvent> getSelectedEvents() {
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

	private class ClockEventsIterable implements Iterable<IClockEvent> {
		private Set<SliceDescriptor> selectedSlices;

		private ClockEventsIterable(Set<SliceDescriptor> selectedSlices) {
			this.selectedSlices = selectedSlices;
		}

		@Override
		public Iterator<IClockEvent> iterator() {
			return new Iterator<IClockEvent>() {
				private final Iterator<SliceDescriptor> outerIterator = selectedSlices.iterator();

				private Iterator<IClockEvent> innerIterator = Collections.emptyIterator();

				@Override
				public void remove() {
				}

				@Override
				public IClockEvent next() {
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
