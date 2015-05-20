package model.util;

import java.util.Calendar;

import com.google.common.base.Joiner;

public final class TimeLabelHelper {
	private static final String[] MONTH_NAMES = { "January", "February", "March", "April", "May", "June", "July",
			"August", "September", "October", "November", "December" };

	private TimeLabelHelper() {
		throw new AssertionError();
	}

	public static String getYearTwoDigitLabel(Calendar calendar) {
		return String.format("'%s", String.valueOf(calendar.get(Calendar.YEAR) % 100));
	}

	public static String getMonthLabel(Calendar calendar) {
		int month = calendar.get(Calendar.MONTH);
		if (month < 0 || month >= MONTH_NAMES.length) {
			throw new AssertionError();
		}
		return MONTH_NAMES[month];
	}

	public static String getDayOfMonthLabel(Calendar calendar) {
		return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static String getHourAndMinuteLabel(Calendar calendar) {
		return Joiner.on(":").join(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}
}
