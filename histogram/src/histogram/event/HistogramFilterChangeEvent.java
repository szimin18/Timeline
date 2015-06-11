package histogram.event;

import histogram.view.Histogram;

import java.util.Date;
import java.util.List;

import model.event.TimelineCategory;

public class HistogramFilterChangeEvent {

	private final Histogram source;

	private final Date beginning;

	private final Date end;
	
	private final int firstElement;
	
	private final int lastElement;

	public HistogramFilterChangeEvent(Histogram source, Date beginning, Date end, int firstElement, int lastElement) {
		this.source = source;
		this.beginning = beginning;
		this.end = end;
		this.firstElement = firstElement;
		this.lastElement = lastElement;
	}

	public Histogram getSource() {
		return source;
	}

	public Date getBeginning() {
		return beginning;
	}

	public Date getEnd() {
		return end;
	}

	public List<TimelineCategory> getFilteredEvents() {
		return source.getFilteredEvents(firstElement, lastElement);
	}
	
}
