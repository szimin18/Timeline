package model.event;

import java.time.LocalDateTime;

import model.id.IDManager;
import model.id.IDManager.TID;

public class TimelineEvent {
	private final TID tid;
	private final LocalDateTime dateTime;
	
	private TimelineEvent(TID timelineID, LocalDateTime localDateTime) {
		dateTime = localDateTime;
		tid = timelineID;
	}
	
	public static TimelineEvent newInstance(LocalDateTime dateTime) {
		return new TimelineEvent(IDManager.generateID(), dateTime);
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}
}
