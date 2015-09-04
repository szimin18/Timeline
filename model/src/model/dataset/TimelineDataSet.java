package model.dataset;

import java.util.List;

import javafx.scene.paint.Color;
import model.event.TimelineEvent;

import com.google.common.collect.Iterables;

public class TimelineDataSet {
	private static final Color[] ALL_COLORS = new Color[] { Color.GOLD, Color.AQUA, Color.GREEN, Color.BLUE, Color.RED };

	private static final IDataSetColorProvider DEFAULT_DATA_SET_COLOR_PROVIDER = new IDataSetColorProvider() {
		private int nextColorIndex = 0;

		@Override
		public Color getDataSetColor(List<TimelineEvent> unsortedEvents) {
			Color result = ALL_COLORS[nextColorIndex];
			nextColorIndex++;
			nextColorIndex %= ALL_COLORS.length;
			return result;
		}
	};

	private final List<TimelineEvent> sortedEvents;
	private final Color color;

	private TimelineDataSet(List<TimelineEvent> unsortedEvents, Color color) {
		this.color = color;
		sortedEvents = Sorter.sortEvents(unsortedEvents);
	}

	public static TimelineDataSet newInstance(List<TimelineEvent> unsortedEvents, Color color) {
		return new TimelineDataSet(unsortedEvents, color);
	}

	public static TimelineDataSet newInstance(List<TimelineEvent> unsortedEvents, IDataSetColorProvider colorProvider) {
		return newInstance(unsortedEvents, colorProvider.getDataSetColor(unsortedEvents));
	}

	public static TimelineDataSet newInstance(List<TimelineEvent> unsortedEvents) {
		return newInstance(unsortedEvents, DEFAULT_DATA_SET_COLOR_PROVIDER.getDataSetColor(unsortedEvents));
	}

	public List<TimelineEvent> getSortedEvents() {
		return sortedEvents;
	}

	public Color getColor() {
		return color;
	}

	public long getTimeSpanMilliseconds() {
		if (sortedEvents.isEmpty()) {
			return 0;
		} else {
			return Iterables.getLast(sortedEvents).getDate().getTime() - sortedEvents.get(0).getDate().getTime();
		}
	}

	/**
	 * A provider for data set colors. Provides a color based on list of data
	 * set events.
	 */
	public static interface IDataSetColorProvider {
		/**
		 * Provides data set color based on unsorted events list. May use this
		 * list to provide a color. When a list of events is not used each call
		 * to this method should return color for next data set. This method is
		 * called once for each data set.
		 * 
		 * @param unsortedEvents
		 *            list of events which can be used for data set color
		 *            computation
		 */
		public Color getDataSetColor(List<TimelineEvent> unsortedEvents);
	}
}
