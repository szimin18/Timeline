package pl.edu.agh.clock.view.chart.selection;

import java.util.Set;

import com.google.common.collect.Sets;

import pl.edu.agh.clock.model.DayOfWeek;
import pl.edu.agh.clock.model.SliceDescriptor;

public class SlicesSelectionManager {
	private final Set<SliceDescriptor> selectedSlices = Sets.newHashSet();

	private final Set<SliceDescriptor> currentlyPressedSlices = Sets.newHashSet();

	private final ISliceSelectionChangeStrategy sliceSelectionChangeStrategy;

	private SliceDescriptor pressedSlice = null;

	private ClockSelectionDirection currentClockPressDirection = ClockSelectionDirection.UNDEFINED;

	private static enum ClockSelectionDirection {
		CLOCKWISE, COUNTERCLOCKWISE, UNDEFINED
	}

	public SlicesSelectionManager(ISliceSelectionChangeStrategy sliceSelectionChangeStrategy) {
		this.sliceSelectionChangeStrategy = sliceSelectionChangeStrategy;
	}

	public void continueSelectingSlices(SliceDescriptor sliceDescriptor) {
		DayOfWeek dayOfWeek = sliceDescriptor.getDayOfWeek();
		int hour = sliceDescriptor.getHour();

		if (pressedSlice != null) {
			DayOfWeek pressedDayOfWeek = pressedSlice.getDayOfWeek();
			int pressedHour = pressedSlice.getHour();

			if (hour == pressedHour) {
				currentClockPressDirection = ClockSelectionDirection.UNDEFINED;
			} else {
				if (currentClockPressDirection == ClockSelectionDirection.UNDEFINED) {
					int hourDifferece = hour - pressedHour;

					if (hourDifferece < 0) {
						hourDifferece += 24;
					}

					if (hourDifferece <= 12) {
						currentClockPressDirection = ClockSelectionDirection.CLOCKWISE;
					} else {
						currentClockPressDirection = ClockSelectionDirection.COUNTERCLOCKWISE;
					}
				}
			}

			currentlyPressedSlices.clear();

			int startingDayOfWeekIndex = pressedDayOfWeek.getIndex();
			int endingDayOfWeekIndex = dayOfWeek.getIndex();

			if (startingDayOfWeekIndex > endingDayOfWeekIndex) {
				startingDayOfWeekIndex = dayOfWeek.getIndex();
				endingDayOfWeekIndex = pressedDayOfWeek.getIndex();
			}

			int startingHour = pressedHour;
			int endingHour = hour;

			if (currentClockPressDirection == ClockSelectionDirection.COUNTERCLOCKWISE) {
				startingHour = hour;
				endingHour = pressedHour;
			}

			for (int dayOfWeekIndex = startingDayOfWeekIndex; dayOfWeekIndex <= endingDayOfWeekIndex; dayOfWeekIndex++) {
				DayOfWeek dayOfWeekToAdd = DayOfWeek.forIndex(dayOfWeekIndex);

				for (int hourToAdd = startingHour;;) {
					currentlyPressedSlices.add(SliceDescriptor.forCoordinates(dayOfWeekToAdd, hourToAdd));

					if (hourToAdd == endingHour) {
						break;
					}

					hourToAdd = (hourToAdd + 1) % 24;
				}
			}
		}

		selectionChanged();
	}

	public void cancelSelectingSlices() {
		pressedSlice = null;
		currentClockPressDirection = ClockSelectionDirection.UNDEFINED;

		if (!currentlyPressedSlices.isEmpty()) {
			currentlyPressedSlices.clear();
			selectionChanged();
		}
	}

	public void finishSelectingSlices() {
		pressedSlice = null;
		currentClockPressDirection = ClockSelectionDirection.UNDEFINED;

		selectedSlices.addAll(currentlyPressedSlices);
		currentlyPressedSlices.clear();
	}

	public void startSelectingSlices(SliceDescriptor sliceDescriptor, boolean keepLastSelection) {
		if (sliceDescriptor != null) {
			pressedSlice = sliceDescriptor;
		}

		if (!keepLastSelection) {
			selectedSlices.clear();
		}

		selectionChanged();
	}

	public Set<SliceDescriptor> getAllSelectedSlices() {
		Set<SliceDescriptor> allSlices = Sets.newHashSet();

		allSlices.addAll(currentlyPressedSlices);
		allSlices.addAll(selectedSlices);

		return allSlices;
	}

	public void selectionChanged() {
		sliceSelectionChangeStrategy.selectionChanged(getAllSelectedSlices());
	}

	public static interface ISliceSelectionChangeStrategy {
		public void selectionChanged(Set<SliceDescriptor> selectedSlices);
	}
}
