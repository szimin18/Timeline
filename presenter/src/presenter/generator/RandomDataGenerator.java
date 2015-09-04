package presenter.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import model.dataset.TimelineDataSet;
import model.event.TimelineEvent;

public class RandomDataGenerator {
	private RandomDataGenerator() {
		throw new AssertionError();
	}

	public static TimelineDataSet generateDataSet(Date startDate, Date endDate, int numberOfItemsToGenerate) {
		long startTime = startDate.getTime();
		long timeStamps = endDate.getTime() - startTime;
		Random random = new Random();

		List<TimelineEvent> events = new ArrayList<>();

		for (int i = 0; i < numberOfItemsToGenerate; i++) {
			events.add(TimelineEvent.newInstance(new Date(startTime + Math.abs(random.nextLong() % timeStamps)), null));
		}

		return TimelineDataSet.newInstance(events);
	}
}
