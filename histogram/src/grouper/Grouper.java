package grouper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import model.event.TimelineEvent;
import model.event.TimelineEventsGroup;

public class Grouper {
	private Grouper() {
		throw new AssertionError();
	}

	public static List<TimelineEventsGroup> group(List<TimelineEvent> events, GroupingMethod groupingMethod) {
		return groupingMethod.group(events);
	}

	public static enum GroupingMethod {
		MONTHS() {
			@Override
			protected List<TimelineEventsGroup> groupSorted(List<TimelineEvent> sortedEvents) {
				LocalDateTime startTime = sortedEvents.get(0).getDateTime();
				LocalDateTime endTime = sortedEvents.get(sortedEvents.size() - 1).getDateTime();

				return IntStream
						.range(startTime.getYear(), endTime.getYear() + 1)
						.mapToObj(new IntFunction<Stream<LocalDateTime>>() {
							@Override
							public Stream<LocalDateTime> apply(int year) {
								return IntStream.rangeClosed(1, 12).mapToObj(
										month -> LocalDateTime.of(year, month, 1, 0, 0));
							}
						})
						.reduce(Stream.empty(), Stream::concat)
						.map(boundaryDate -> sortedEvents
								.stream()
								.filter(date -> !date.getDateTime().isBefore(boundaryDate)
										&& date.getDateTime().isBefore(boundaryDate.plusMonths(1)))
								.collect(Collectors.toList()))
						.filter(events -> !events.isEmpty())
						.map(eventsList -> TimelineEventsGroup.newInstance(
								eventsList,
								String.format("%s '%d", eventsList.get(0).getDateTime().getMonth(), eventsList.get(0)
										.getDateTime().getYear()))).collect(Collectors.toList());
			}
		};

		private List<TimelineEventsGroup> group(List<TimelineEvent> events) {
			List<TimelineEvent> sortedEvents = events.stream()
					.sorted((event1, event2) -> event1.getDateTime().compareTo(event2.getDateTime()))
					.collect(Collectors.toList());
			if (sortedEvents.isEmpty()) {
				throw new AssertionError();
			}
			return groupSorted(sortedEvents);

		}

		protected abstract List<TimelineEventsGroup> groupSorted(List<TimelineEvent> sortedEvents);
	}
}
