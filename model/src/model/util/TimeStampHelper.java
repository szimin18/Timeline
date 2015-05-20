package model.util;

public final class TimeStampHelper {
	private TimeStampHelper() {
		throw new AssertionError();
	}
	
	public static long secondInMiliseconds() {
		return 1000l;
	}
	
	public static long minuteInMiliseconds() {
		return 1000l * 60;
	}
	
	public static long hourInMiliseconds() {
		return 1000l * 60 * 60;
	}
	
	public static long dayInMiliseconds() {
		return 1000l * 60 * 60 * 24;
	}
	
	public static long monthEstimateInMiliseconds() {
		long i = 1000l * 60 * 60 * 24 * 30; // < 0 !!!!!
		return i;
	}
	
	public static long yearEstimateInMiliseconds() {
		return (long)(1000l * 60 * 60 * 24 * 365.25);
	}
}
