package com.android.lazarus.bus.model;

public class BusStop {
	private double latitude;
	private double longitude;
	private int ordinal;

	public BusStop(double latitude, double longitude, int ordinal) {
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

	public int getOrdinal() {
		return ordinal;
	}
}
