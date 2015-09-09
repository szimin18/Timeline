package clock.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DayOfWeek {
	MONDAY {
		@Override
		public String toString() {
			return "Monday";
		}

		@Override
		public String getShortNameCapital() {
			return "MON";
		}

		@Override
		public int getIndex() {
			return 0;
		}
	},
	TUESDAY {
		@Override
		public String toString() {
			return "Tuesday";
		}

		@Override
		public String getShortNameCapital() {
			return "TUE";
		}

		@Override
		public int getIndex() {
			return 1;
		}
	},
	WEDNESDAY {
		@Override
		public String toString() {
			return "Wednesday";
		}

		@Override
		public String getShortNameCapital() {
			return "WED";
		}

		@Override
		public int getIndex() {
			return 2;
		}
	},
	THURSDAY {
		@Override
		public String toString() {
			return "Thursday";
		}

		@Override
		public String getShortNameCapital() {
			return "THU";
		}

		@Override
		public int getIndex() {
			return 3;
		}
	},
	FRIDAY {
		@Override
		public String toString() {
			return "Friday";
		}

		@Override
		public String getShortNameCapital() {
			return "FRI";
		}

		@Override
		public int getIndex() {
			return 4;
		}
	},
	SATURDAY {
		@Override
		public String toString() {
			return "Saturday";
		}

		@Override
		public String getShortNameCapital() {
			return "SAT";
		}

		@Override
		public int getIndex() {
			return 5;
		}
	},
	SUNDAY {
		@Override
		public String toString() {
			return "Sunday";
		}

		@Override
		public String getShortNameCapital() {
			return "SUN";
		}

		@Override
		public int getIndex() {
			return 6;
		}
	};

	public static final List<DayOfWeek> VALUES_LIST = new ArrayList<>();

	private static final Map<Integer, DayOfWeek> CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP = new HashMap<>();

	private static final Map<Integer, DayOfWeek> INDEX_TO_DAY_OF_WEEK_MAP = new HashMap<>();

	static {
		VALUES_LIST.add(MONDAY);
		VALUES_LIST.add(TUESDAY);
		VALUES_LIST.add(WEDNESDAY);
		VALUES_LIST.add(THURSDAY);
		VALUES_LIST.add(FRIDAY);
		VALUES_LIST.add(SATURDAY);
		VALUES_LIST.add(SUNDAY);

		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.MONDAY, MONDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.TUESDAY, TUESDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.WEDNESDAY, WEDNESDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.THURSDAY, THURSDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.FRIDAY, FRIDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.SATURDAY, SATURDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.SUNDAY, SUNDAY);

		INDEX_TO_DAY_OF_WEEK_MAP.put(MONDAY.getIndex(), MONDAY);
		INDEX_TO_DAY_OF_WEEK_MAP.put(TUESDAY.getIndex(), TUESDAY);
		INDEX_TO_DAY_OF_WEEK_MAP.put(WEDNESDAY.getIndex(), WEDNESDAY);
		INDEX_TO_DAY_OF_WEEK_MAP.put(THURSDAY.getIndex(), THURSDAY);
		INDEX_TO_DAY_OF_WEEK_MAP.put(FRIDAY.getIndex(), FRIDAY);
		INDEX_TO_DAY_OF_WEEK_MAP.put(SATURDAY.getIndex(), SATURDAY);
		INDEX_TO_DAY_OF_WEEK_MAP.put(SUNDAY.getIndex(), SUNDAY);
	}

	@Override
	public abstract String toString();

	public abstract String getShortNameCapital();

	public abstract int getIndex();

	public static DayOfWeek forCalendarIndex(int index) throws AssertionError {
		if (CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.containsKey(index)) {
			return CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.get(index);
		} else {
			throw new AssertionError();
		}
	}

	public static DayOfWeek forIndex(int index) throws AssertionError {
		if (INDEX_TO_DAY_OF_WEEK_MAP.containsKey(index)) {
			return INDEX_TO_DAY_OF_WEEK_MAP.get(index);
		} else {
			throw new AssertionError();
		}
	}
}
