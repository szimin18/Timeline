package model.event;

import java.util.Date;

import model.id.IDManager;
import model.id.IDManager.TID;

public class TimelineEvent {
	private final TID timelineID;
	private final Date date;
	private final Object referencedObject;

	private TimelineEvent(TID timelineID, Date date, Object referencedObject) {
		this.date = date;
		this.timelineID = timelineID;
		this.referencedObject = referencedObject;
	}

	public static TimelineEvent newInstance(Date dateTime, Object referencedObject) {
		return new TimelineEvent(IDManager.generateID(), dateTime, referencedObject);
	}

	public Date getDate() {
		return date;
	}

	public TID getTid() {
		return timelineID;
	}

	public Object getReferencedObject() {
		return referencedObject;
	}
}
