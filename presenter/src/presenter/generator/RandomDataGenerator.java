package presenter.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import clock.model.ClockDataSet;
import clock.model.IClockEvent;

public class RandomDataGenerator {
	private RandomDataGenerator() {
		throw new AssertionError();
	}

	public static ClockDataSet generateClockDataSet(Date startDate, Date endDate, int numberOfItemsToGenerate) {
		long startTime = startDate.getTime();
		long timeStamps = endDate.getTime() - startTime;
		Random random = new Random();

		List<IClockEvent> events = new ArrayList<>();

		for (int i = 0; i < numberOfItemsToGenerate; i++) {
			final Date date = new Date(startTime + Math.abs(random.nextLong() % timeStamps));

			events.add(new IClockEvent() {
				@Override
				public Date getDate() {
					return date;
				}
			});
		}

		return ClockDataSet.newInstance(events);
	}
}
