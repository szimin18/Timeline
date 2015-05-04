package grouper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
				Calendar startCalendar = Calendar.getInstance();
				startCalendar.setTime(sortedEvents.get(0).getDate());
				Calendar endCalendar = Calendar.getInstance();
				endCalendar.setTime(sortedEvents.get(sortedEvents.size() - 1).getDate());
				int endYear = endCalendar.get(Calendar.YEAR);
				int endMonth = endCalendar.get(Calendar.MONTH);

				Calendar tmpCalendar = Calendar.getInstance();

				List<TimelineEventsGroup> result = new ArrayList<TimelineEventsGroup>();

				for (int year = startCalendar.get(Calendar.YEAR);; year++) {
					for (int month = startCalendar.get(Calendar.MONTH); month < 12; month++) {
						List<TimelineEvent> eventsList = new ArrayList<>();
						for (TimelineEvent event : sortedEvents) {
							tmpCalendar.setTime(event.getDate());
							if (tmpCalendar.get(Calendar.YEAR) == year && tmpCalendar.get(Calendar.MONTH) == month) {
								eventsList.add(event);
							}
						}
						result.add(TimelineEventsGroup.newInstance(eventsList,
								String.format("%s '%d", getMonthName(month), year % 100)));
						if (year == endYear && month == endMonth) {
							return result;
						}
					}
				}
			}

			private String getMonthName(int month) {
				switch (month) {
				case 0:
					return "January";
				case 1:
					return "February";
				case 2:
					return "March";
				case 3:
					return "April";
				case 4:
					return "May";
				case 5:
					return "June";
				case 6:
					return "July";
				case 7:
					return "August";
				case 8:
					return "September";
				case 9:
					return "October";
				case 10:
					return "November";
				case 11:
					return "December";
				default:
					return "Unknown";
				}
			}
		};

		private List<TimelineEventsGroup> group(List<TimelineEvent> events) {
			if (events.isEmpty()) {
				throw new AssertionError();
			}
			List<TimelineEvent> sortedEvents = events;
			Collections.sort(sortedEvents, new Comparator<TimelineEvent>() {
				@Override
				public int compare(TimelineEvent o1, TimelineEvent o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			});
			return groupSorted(sortedEvents);

		}

		protected abstract List<TimelineEventsGroup> groupSorted(List<TimelineEvent> sortedEvents);
	}
}
