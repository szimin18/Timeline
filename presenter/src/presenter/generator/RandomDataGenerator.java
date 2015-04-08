package presenter.generator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.event.TimelineEvent;

public class RandomDataGenerator {
	private RandomDataGenerator() {
		throw new AssertionError();
	}

	public static List<TimelineEvent> generateRandomEvents(LocalDateTime startDateTime, LocalDateTime endDateTime,
			int numberOfItemsToGenerate) {
		long timeStamps = ChronoUnit.MINUTES.between(startDateTime, endDateTime);
		Random random = new Random();

		return IntStream
				.generate(() -> 0)
				.limit(numberOfItemsToGenerate)
				.mapToObj(
						number -> TimelineEvent.newInstance(startDateTime.plusMinutes(random.nextLong() % timeStamps)))
				.collect(Collectors.toList());
	}
}
