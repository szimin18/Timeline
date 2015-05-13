package model.event;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class TimelineCategory {
	private final List<TimelineChartData> timelineChartDataList = new ArrayList<>();
	private final Color color;

	public TimelineCategory(Color color) {
		this.color = color;
	}

	public void addTimelineChartData(TimelineChartData data) {
		timelineChartDataList.add(data);
	}

	public List<TimelineChartData> getTimelineChartDataList() {
		return timelineChartDataList;
	}

	public String getColorHex() {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	public Color getColor() {
		return color;
	}
	
}
