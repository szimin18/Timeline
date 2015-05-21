package histogram.grouper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.dataset.TimelineDataSet;
import model.event.TimelineCategory;
import model.event.TimelineChartData;
import model.event.TimelineEvent;
import model.util.TimeLabelHelper;
import model.util.TimeStampHelper;

import com.google.common.base.Joiner;

public class Grouper {

	private static final Map<Integer, Integer> DEFAULT_CALENDAR_VALUES;

	static {
		DEFAULT_CALENDAR_VALUES = new HashMap<>();
		DEFAULT_CALENDAR_VALUES.put(Calendar.MONTH, Calendar.JANUARY);
		DEFAULT_CALENDAR_VALUES.put(Calendar.DAY_OF_MONTH, 1);
		DEFAULT_CALENDAR_VALUES.put(Calendar.HOUR, 0);
		DEFAULT_CALENDAR_VALUES.put(Calendar.MINUTE, 0);
		DEFAULT_CALENDAR_VALUES.put(Calendar.SECOND, 0);
		DEFAULT_CALENDAR_VALUES.put(Calendar.MILLISECOND, 0);
	}

	private Grouper() {
		throw new AssertionError();
	}

	public static List<TimelineCategory> group(List<TimelineDataSet> timelineDataSets, GroupingMethod groupingMethod) {
		return groupingMethod.group(timelineDataSets);
	}

	public static GroupingMethod getDefaultGroupingMethodForDatasets(List<TimelineDataSet> timelineDataSets) {
		return GroupingMethod.defaultForDatasets(timelineDataSets);
	}

	public static enum GroupingMethod {
		YEARS {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.YEAR, 1);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return TimeLabelHelper.getYearTwoDigitLabel(calendar);
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.yearEstimateInMiliseconds();
			}
		},
		MONTHS_3 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.MONTH, 3);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getYearTwoDigitLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.monthEstimateInMiliseconds() * 3;
			}
		},
		MONTHS_1 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.MONTH, 1);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getYearTwoDigitLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.monthEstimateInMiliseconds();
			}
		},
		DAYS_10 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.DAY_OF_MONTH, 10);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.dayInMiliseconds() * 10;
			}
		},
		DAYS_3 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.DAY_OF_MONTH, 3);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.dayInMiliseconds() * 3;
			}
		},
		DAYS_1 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.dayInMiliseconds();
			}
		},
		HOURS_8 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.HOUR_OF_DAY, 8);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.hourInMiliseconds() * 8;
			}
		},
		HOURS_3 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.HOUR_OF_DAY, 3);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.hourInMiliseconds() * 3;
			}
		},
		HOURS_1 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.HOUR_OF_DAY, 1);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.hourInMiliseconds();
			}
		},
		MINUTES_20 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.MINUTE, 20);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.minuteInMiliseconds() * 20;
			}
		},
		MINUTES_8 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.MINUTE, 8);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.minuteInMiliseconds() * 8;
			}
		},
		MINUTES_3 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.MINUTE, 3);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.minuteInMiliseconds() * 3;
			}
		},
		MINUTES_1 {
			@Override
			protected void setInitialTime(Calendar calendar) {
				setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE);
			}

			@Override
			protected void setNextTime(Calendar calendar) {
				calendar.add(Calendar.MINUTE, 1);
			}

			@Override
			protected String getTimeDescription(Calendar calendar) {
				return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
						TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
			}

			@Override
			protected long getEstimatedGroupSpanInMillis() {
				return TimeStampHelper.minuteInMiliseconds();
			}
		},
        SECONDS_20 {
            @Override
            protected void setInitialTime(Calendar calendar) {
                setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND);
            }

            @Override
            protected void setNextTime(Calendar calendar) {
                calendar.add(Calendar.MINUTE, 20);
            }

            @Override
            protected String getTimeDescription(Calendar calendar) {
                return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
                        TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
            }

            @Override
            protected long getEstimatedGroupSpanInMillis() {
                return TimeStampHelper.secondInMiliseconds() * 20;
            }
        },
        SECONDS_8 {
            @Override
            protected void setInitialTime(Calendar calendar) {
                setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND);
            }

            @Override
            protected void setNextTime(Calendar calendar) {
                calendar.add(Calendar.MINUTE, 8);
            }

            @Override
            protected String getTimeDescription(Calendar calendar) {
                return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
                        TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
            }

            @Override
            protected long getEstimatedGroupSpanInMillis() {
                return TimeStampHelper.secondInMiliseconds() * 8;
            }
        },
        SECONDS_3 {
            @Override
            protected void setInitialTime(Calendar calendar) {
                setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND);
            }

            @Override
            protected void setNextTime(Calendar calendar) {
                calendar.add(Calendar.MINUTE, 3);
            }

            @Override
            protected String getTimeDescription(Calendar calendar) {
                return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
                        TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
            }

            @Override
            protected long getEstimatedGroupSpanInMillis() {
                return TimeStampHelper.secondInMiliseconds() * 3;
            }
        },
        SECONDS_1 {
            @Override
            protected void setInitialTime(Calendar calendar) {
                setFieldsDefaultsRetainingGiven(calendar, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND);
            }

            @Override
            protected void setNextTime(Calendar calendar) {
                calendar.add(Calendar.MINUTE, 1);
            }

            @Override
            protected String getTimeDescription(Calendar calendar) {
                return Joiner.on(' ').join(TimeLabelHelper.getMonthLabel(calendar), TimeLabelHelper.getDayOfMonthLabel(calendar),
                        TimeLabelHelper.getYearTwoDigitLabel(calendar), TimeLabelHelper.getHourAndMinuteLabel(calendar));
            }

            @Override
            protected long getEstimatedGroupSpanInMillis() {
                return TimeStampHelper.secondInMiliseconds();
            }
        };

		private static final int TARGET_NUMBER_OF_BARS = 50;

		private static final GroupingMethod defaultForDatasets(List<TimelineDataSet> timelineDataSets) {
			Set<Long> timeSpansSet = new HashSet<>();
			for (TimelineDataSet dataSet : timelineDataSets) {
				timeSpansSet.add(dataSet.getTimeSpanMilliseconds());
			}
			long maxSpan = Collections.max(timeSpansSet);
			GroupingMethod bestMethod = null;
			double bestRatio = 0;
			for (GroupingMethod method : GroupingMethod.values()) {
				System.out.printf("%s - %f bars\n", method, (double) maxSpan / method.getEstimatedGroupSpanInMillis());
				double newRatio = (double) method.getEstimatedGroupSpanInMillis() * TARGET_NUMBER_OF_BARS / maxSpan;
				if (newRatio > 1) {
					newRatio = 1 / newRatio;
				}
				if (newRatio > bestRatio) {
					bestRatio = newRatio;
					bestMethod = method;
				}
			}
			System.out.printf("Choosen method: %s\n\n", bestMethod);
			return bestMethod;
		}

		private static final void setFieldsDefaultsRetainingGiven(Calendar calendar, int... retainedFields) {
			Set<Integer> retainedFieldsSet = new HashSet<Integer>(retainedFields.length);
			for (int fieldName : retainedFields) {
				retainedFieldsSet.add(fieldName);
			}

			for (Integer key : DEFAULT_CALENDAR_VALUES.keySet()) {
				if (!retainedFieldsSet.contains(key)) {
					calendar.set(key, DEFAULT_CALENDAR_VALUES.get(key));
				}
			}
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

					timelineCategory.addTimelineChartData(new TimelineChartData(eventsList, timeRange.getDescription(), timeRange
							.getStartTime(), timeRange.getEndTime()));
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

		protected abstract long getEstimatedGroupSpanInMillis();

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

		public Date getStartTime() {
			return new Date(startTime);
		}

		public Date getEndTime() {
			return new Date(endTime);
		}
	}
}
