package grouper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import model.dataset.TimelineDataSet;
import model.event.TimelineCategory;
import model.event.TimelineChartData;
import model.event.TimelineEvent;

import com.google.common.base.Joiner;

public class Grouper {
	private Grouper() {
		throw new AssertionError();
	}

	public static List<TimelineCategory> group(List<TimelineDataSet> timelineDataSets, GroupingMethod groupingMethod) {
		return groupingMethod.group(timelineDataSets);
	}

	public static enum GroupingMethod {
		MONTHS() {
			@Override
			protected void setInitialTime(Calendar calendar) {
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.MONTH, 1);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(getMonthLabel(calendar.get(Calendar.MONTH)),
						getYearLabel(calendar.get(Calendar.YEAR)));
			}
		},

		DAYS() {
			@Override
			protected void setInitialTime(Calendar calendar) {
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH));
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(getMonthLabel(calendar.get(Calendar.MONTH)),
						getDayLabel(calendar.get(Calendar.DAY_OF_MONTH)), getYearLabel(calendar.get(Calendar.YEAR)));
			}
		};

		public static final GroupingMethod defaultForDatasets(List<TimelineDataSet> timelineDataSets) {
			return MONTHS;
		}

		private static final String getYearLabel(int year) {
			return "'" + String.valueOf(year % 100);
		}

		private static final String getMonthLabel(int month) {
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

		private static final String getDayLabel(int day) {
			return String.valueOf(day);
		}

		private final List<TimelineCategory> group(List<TimelineDataSet> timelineDataSets) {
			List<Date> firstDates = new ArrayList<Date>(timelineDataSets.size());
			List<EventsProvider> eventProviders = new ArrayList<>(timelineDataSets.size());
			List<TimelineCategory> timelineCategories = new ArrayList<>(timelineDataSets.size());

			for (TimelineDataSet timelineDataSet : timelineDataSets) {
				EventsProvider eventsProvider = new EventsProvider(timelineDataSet.getSortedEvents());

				if (eventsProvider.hasMoreEvents()) {
					firstDates.add(eventsProvider.getFirstEvent().getDate());
					eventProviders.add(eventsProvider);
					timelineCategories.add(new TimelineCategory(timelineDataSet.getColor()));
				}
			}

			if (firstDates.isEmpty()) {
				throw new AssertionError();
			}

			Collections.sort(firstDates);

			final int eventProvidersCount = eventProviders.size();

			for (TimeRange timeRange : new InfiniteRangeIterable(firstDates.get(0))) {
				for (int i = 0; i < eventProvidersCount; i++) {
					EventsProvider eventsProvider = eventProviders.get(i);
					TimelineCategory timelineCategory = timelineCategories.get(i);

					List<TimelineEvent> eventsList = new ArrayList<>();

					while (eventsProvider.hasMoreEventsFromRange(timeRange)) {
						eventsList.add(eventsProvider.getNextEvent());
					}

					timelineCategory
							.addTimelineChartData(new TimelineChartData(eventsList, timeRange.getDescription()));
				}

				boolean anySeriesHasMoreEvents = false;

				for (EventsProvider eventsProvider : eventProviders) {
					if (eventsProvider.hasMoreEvents()) {
						anySeriesHasMoreEvents = true;
						break;
					}
				}

				if (!anySeriesHasMoreEvents) {
					break;
				}
			}

			return timelineCategories;
		}

		protected abstract void setInitialTime(Calendar calendar);

		protected abstract void setNextTime(Calendar calendar);

		protected abstract String getTimeDescription(Calendar calendar);

		private class InfiniteRangeIterable implements Iterable<TimeRange> {
			private final Date firstDate;

			public InfiniteRangeIterable(Date firstDate) {
				this.firstDate = firstDate;
			}

			@Override
			public Iterator<TimeRange> iterator() {
				return new Iterator<Grouper.TimeRange>() {
					private final Calendar calendar = Calendar.getInstance();
					{
						calendar.setTime(firstDate);
						setInitialTime(calendar);
					}

					@Override
					public boolean hasNext() {
						return true;
					}

					@Override
					public void remove() {
					}

					@Override
					public TimeRange next() {
						long startTime = calendar.getTimeInMillis();
						String description = getTimeDescription(calendar);
						setNextTime(calendar);
						long endTime = calendar.getTimeInMillis();
						TimeRange result = new TimeRange(startTime, endTime, description);
						return result;
					}
				};
			}
		}
	}

	private static final class EventsProvider {
		private int currentIndex = 0;
		private final List<TimelineEvent> eventsList;
		private final int eventsCount;

		public EventsProvider(List<TimelineEvent> eventsList) {
			this.eventsList = eventsList;
			eventsCount = eventsList.size();
		}

		public boolean hasMoreEvents() {
			return currentIndex < eventsCount;
		}

		public boolean hasMoreEventsFromRange(TimeRange timeRange) {
			return hasMoreEvents() && timeRange.contains(eventsList.get(currentIndex).getDate());
		}

		public TimelineEvent getNextEvent() {
			return eventsList.get(currentIndex++);
		}

		public TimelineEvent getFirstEvent() {
			return eventsList.get(0);
		}
	}

	private static final class TimeRange {
		private final long startTime;

		private final long endTime;

		private final String description;

		public TimeRange(long startTime, long endTime, String description) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.description = description;
		}

		public boolean contains(Date date) {
			if (date == null) {
				return false;
			}
			long time = date.getTime();
			return startTime <= time && time < endTime;
		}

		public String getDescription() {
			return description;
		}
	}
}
