package clock.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DayOfWeek {
	MONDAY {
		@Override
		protected String getStringRepresentation() {
			return "Monday";
		}
	},
	TUESDAY {
		@Override
		protected String getStringRepresentation() {
			return "Tuesday";
		}
	},
	WEDNESDAY {
		@Override
		protected String getStringRepresentation() {
			return "Wednesday";
		}
	},
	THURSDAY {
		@Override
		protected String getStringRepresentation() {
			return "Thursday";
		}
	},
	FRIDAY {
		@Override
		protected String getStringRepresentation() {
			return "Friday";
		}
	},
	SATURDAY {
		@Override
		protected String getStringRepresentation() {
			return "Saturday";
		}
	},
	SUNDAY {
		@Override
		protected String getStringRepresentation() {
			return "Sunday";
		}
	};

	public static final List<DayOfWeek> VALUES_LIST = new ArrayList<>();

	static {
		VALUES_LIST.add(MONDAY);
		VALUES_LIST.add(TUESDAY);
		VALUES_LIST.add(WEDNESDAY);
		VALUES_LIST.add(THURSDAY);
		VALUES_LIST.add(FRIDAY);
		VALUES_LIST.add(SATURDAY);
		VALUES_LIST.add(SUNDAY);
	}

	private static final Map<Integer, DayOfWeek> CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP = new HashMap<>();

	static {
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.MONDAY, MONDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.TUESDAY, TUESDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.WEDNESDAY, WEDNESDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.THURSDAY, THURSDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.FRIDAY, FRIDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.SATURDAY, SATURDAY);
		CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.put(Calendar.SUNDAY, SUNDAY);
	}

	private DayOfWeek() {
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public static DayOfWeek forCalendarIndex(int index) throws AssertionError {
		if (CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.containsKey(index)) {
			return CALENDAR_INDEX_TO_DAY_OF_WEEK_MAP.get(index);
		} else {
			throw new AssertionError();
		}
	}

	protected abstract String getStringRepresentation();
}
