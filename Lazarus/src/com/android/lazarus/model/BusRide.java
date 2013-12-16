package com.android.lazarus.model;

import java.util.List;

public class BusRide {
	public static final int precision = 200;

	private String lineName;
	private String subLineDescription;
	private int subLineCode;
	private BusStop startStop;
	private BusStop endStop;
	private List<Point> trajectory;
	private List<BusStop> previousStops;
	private String destination;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public BusStop getStartStop() {
		return startStop;
	}

	public void setStartStop(BusStop startStop) {
		this.startStop = startStop;
	}

	public BusStop getEndStop() {
		return endStop;
	}

	public void setEndStop(BusStop endStop) {
		this.endStop = endStop;
	}

	public List<Point> getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(List<Point> trajectory) {
		this.trajectory = trajectory;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getSubLineDescription() {
		return subLineDescription;
	}

	public void setSubLineDescription(String subLineDescription) {
		this.subLineDescription = subLineDescription;
	}

	public int getSubLineCode() {
		return subLineCode;
	}

	public void setSubLineCode(int subLineCode) {
		this.subLineCode = subLineCode;
	}

	public List<BusStop> getPreviousStops() {
		return previousStops;
	}

	public void setPreviousStops(List<BusStop> previousStops) {
		this.previousStops = previousStops;
	}
}
