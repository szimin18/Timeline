package model.event;

import java.util.Date;

import model.id.IDManager;
import model.id.IDManager.TID;

public class TimelineEvent {
	private final TID timelineID;
	private final Date date;
	
	private TimelineEvent(TID timelineID, Date date) {
		this.date = date;
		this.timelineID = timelineID;
	}
	
	public static TimelineEvent newInstance(Date dateTime) {
		return new TimelineEvent(IDManager.generateID(), dateTime);
	}

	public Date getDate() {
		return date;
	}

	public TID getTid() {
		return timelineID;
	}
}
