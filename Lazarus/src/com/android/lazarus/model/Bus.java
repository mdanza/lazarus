package com.android.lazarus.model;

import java.util.Date;

public class Bus {
	private long id;

	private long variantCode;

	private long subLineCode;

	private Date lastUpdated;

	private long lastPassedStopOrdinal;

	private double latitude;

	private double longitude;

	public long getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(long variantCode) {
		this.variantCode = variantCode;
	}

	public long getSubLineCode() {
		return subLineCode;
	}

	public void setSubLineCode(long subLineCode) {
		this.subLineCode = subLineCode;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public long getLastPassedStopOrdinal() {
		return lastPassedStopOrdinal;
	}

	public void setLastPassedStopOrdinal(long lastPassedStopOrdinal) {
		this.lastPassedStopOrdinal = lastPassedStopOrdinal;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
