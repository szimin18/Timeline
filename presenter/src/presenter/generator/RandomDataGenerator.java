package presenter.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import model.event.TimelineEvent;

public class RandomDataGenerator {
	private RandomDataGenerator() {
		throw new AssertionError();
	}

	public static List<TimelineEvent> generateRandomEvents(Date startDate, Date endDate, int numberOfItemsToGenerate) {
		long startTime = startDate.getTime();
		long timeStamps = endDate.getTime() - startTime;
		Random random = new Random();

		List<TimelineEvent> result = new ArrayList<>();

		for (int i = 0; i < numberOfItemsToGenerate; i++) {
			result.add(TimelineEvent.newInstance(new Date(startTime + random.nextLong() % timeStamps)));
		}

		return result;
	}
}
