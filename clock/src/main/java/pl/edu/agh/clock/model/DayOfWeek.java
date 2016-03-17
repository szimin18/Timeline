package pl.edu.agh.clock.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.text.Font;

public enum DayOfWeek {
	MONDAY("Monday", "MON", 0),

	TUESDAY("Tuesday", "TUE", 1),

	WEDNESDAY("Wednesday", "WED", 2),

	THURSDAY("Thursday", "THU", 3),

	FRIDAY("Friday", "FRI", 4),

	SATURDAY("Saturday", "SAT", 5),

	SUNDAY("Sunday", "SUN", 6);

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

	public static final String LONGEST_DAY_OF_WEEK_NAME;

	static {
		FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
		FontMetrics fontMetrics = fontLoader.getFontMetrics(new Font(fontLoader.getSystemFontSize()));

		String longestDayOfWeekName = ""; //$NON-NLS-1$

		for (DayOfWeek dayOfWeek : DayOfWeek.VALUES_LIST) {
			String currentDayOfWeekName = dayOfWeek.getShortNameCapital();

			if (fontMetrics.computeStringWidth(currentDayOfWeekName) > fontMetrics.computeStringWidth(longestDayOfWeekName)) {
				longestDayOfWeekName = currentDayOfWeekName;
			}
		}

		LONGEST_DAY_OF_WEEK_NAME = longestDayOfWeekName;
	}

	private final String stringRepresentation;

	private final String shortNameCapital;

	private final int index;

	private DayOfWeek(String stringRepresentation, String shortNameCapital, int index) {
		this.stringRepresentation = stringRepresentation;
		this.shortNameCapital = shortNameCapital;
		this.index = index;
	}

	@Override
	public String toString() {
		return stringRepresentation;
	}

	public String getShortNameCapital() {
		return shortNameCapital;
	}

	public int getIndex() {
		return index;
	}

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
