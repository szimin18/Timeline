package histogram.event;

import histogram.view.Histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.event.TimelineChartData;
import model.event.TimelineEvent;

public class HistogramSelectionChangeEvent {
	private final Histogram source;
	
	private final Set<TimelineChartData> selectedData;

	public HistogramSelectionChangeEvent(Histogram source, Set<TimelineChartData> selectedData) {
		this.source = source;
		this.selectedData = selectedData;
	}
	
	public Histogram getSource() {
		return source;
	}
	
	public List<TimelineEvent> getSelectedEvents() {
		List<TimelineEvent> result = new ArrayList<>();
		for (TimelineChartData data: selectedData) {
			result.addAll(data.getEventsList());
		}
		return result;
	}
	
	
}
