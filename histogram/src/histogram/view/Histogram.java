package histogram.view;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.GridPane;
import model.event.TimelineEvent;
import model.event.TimelineEventsGroup;

public class Histogram extends GridPane {
	private final List<TimelineEvent> events;

	private Histogram(List<TimelineEvent> events, boolean isBarChart) {
		this.events = events;
		
		CategoryAxis xAxis = new CategoryAxis();

		NumberAxis yAxis = new NumberAxis();
		
		if (isBarChart) {

			BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
			
			add(barChart, 0, 0);
			setFillHeight(barChart, true);
			setFillWidth(barChart, true);
	
			barChart.setTitle("Histogram");
			xAxis.setLabel("Event time ranges");
			yAxis.setLabel("Number of occurences");
	
			Series<String, Number> series = new Series<>();
	
			series.setName("events series");
	
			groupEvents(events).forEach(
					eventsGroup -> series.getData().add(
							new Data<String, Number>(eventsGroup.toString(), eventsGroup.getEventsCount())));
	
			barChart.getData().add(series);
			barChart.setBarGap(0);
			barChart.setCategoryGap(0);
		} else {
			
			LineChart<String, Number> lineChart = new LineChart(xAxis, yAxis);
	
			add(lineChart, 0, 0);
			setFillHeight(lineChart, true);
			setFillWidth(lineChart, true);
	
			lineChart.setTitle("Histogram");
			xAxis.setLabel("Event time ranges");
			yAxis.setLabel("Number of occurences");
	
			Series<String, Number> series = new Series<>();
	
			series.setName("events series");
	
			groupEvents(events).forEach(
					eventsGroup -> series.getData().add(
							new Data<String, Number>(eventsGroup.toString(), eventsGroup.getEventsCount())));
	
			lineChart.getData().add(series);
		}
	}

	private static List<TimelineEventsGroup> groupEvents(List<TimelineEvent> events) {
		List<TimelineEvent> sortedEvents = events.stream()
				.sorted((event1, event2) -> event1.getDateTime().compareTo(event2.getDateTime()))
				.collect(Collectors.toList());

		final int numberOfBars = 20;

		LocalDateTime startTime = sortedEvents.get(0).getDateTime();
		LocalDateTime endTime = sortedEvents.get(sortedEvents.size() - 1).getDateTime();

		Duration groupDuration = Duration.between(startTime, endTime).dividedBy(numberOfBars);

		return IntStream
				.range(0, numberOfBars)
				.mapToObj(
						index -> {
							LocalDateTime periodStartTime = startTime.plus(groupDuration.multipliedBy(index));
							LocalDateTime periodEndTime = startTime.plus(groupDuration.multipliedBy(index + 1));
							if (index + 1 == numberOfBars) {
								return sortedEvents
										.stream()
										.filter(event -> (event.getDateTime().isAfter(periodStartTime) || event
												.getDateTime().isEqual(periodStartTime))
												&& event.getDateTime().isBefore(periodEndTime))
										.collect(Collectors.toList());
							} else {
								return sortedEvents
										.stream()
										.filter(event -> (event.getDateTime().isAfter(periodStartTime) || event
												.getDateTime().isEqual(periodStartTime))
												&& (event.getDateTime().isBefore(periodEndTime) || event.getDateTime()
														.isEqual(periodEndTime))).collect(Collectors.toList());
							}
						}).map(eventsList -> TimelineEventsGroup.newInstance(eventsList)).collect(Collectors.toList());

	}

	public static Histogram newInstance(List<TimelineEvent> events, boolean barChart) {
		return new Histogram(events, barChart);
	}
}
