package com.android.lazarus.model;

public class BusStop{

	private long busStopLocationCode;

	private long variantCode;

	private long ordinal;

	private String streetName;

	private long streetCode;

	private String cornerStreetName;

	private long cornerStreetCode;
	
	private boolean active;

	private Point point;

	public BusStop(){
		this.active = true;
	}
	
	public BusStop(BusStop anotherBusStop){
		this.busStopLocationCode = anotherBusStop.busStopLocationCode;
		this.variantCode = anotherBusStop.variantCode;
		this.ordinal = anotherBusStop.ordinal;
		this.streetName = anotherBusStop.streetName;
		this.streetCode = anotherBusStop.streetCode;
		this.cornerStreetName = anotherBusStop.cornerStreetName;
		this.cornerStreetCode = anotherBusStop.cornerStreetCode;
		this.point = anotherBusStop.point;
		this.active = anotherBusStop.active;
	}

	public long getBusStopLocationCode() {
		return busStopLocationCode;
	}

	public void setBusStopLocationCode(long busStopLocationCode) {
		this.busStopLocationCode = busStopLocationCode;
	}

	public long getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(long variantCode) {
		this.variantCode = variantCode;
	}

	public long getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(long ordinal) {
		this.ordinal = ordinal;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public long getStreetCode() {
		return streetCode;
	}

	public void setStreetCode(long streetCode) {
		this.streetCode = streetCode;
	}

	public String getCornerStreetName() {
		return cornerStreetName;
	}

	public void setCornerStreetName(String cornerStreetName) {
		this.cornerStreetName = cornerStreetName;
	}

	public long getCornerStreetCode() {
		return cornerStreetCode;
	}

	public void setCornerStreetCode(long cornerStreetCode) {
		this.cornerStreetCode = cornerStreetCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
}
