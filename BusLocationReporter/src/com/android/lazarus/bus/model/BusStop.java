package com.android.lazarus.bus.model;

public class BusStop {
	private double latitude;
	private double longitude;
	private long ordinal;

	public BusStop(double latitude, double longitude, long ordinal) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.ordinal = ordinal;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public long getOrdinal() {
		return ordinal;
	}
}
