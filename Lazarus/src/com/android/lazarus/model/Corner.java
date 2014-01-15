package com.android.lazarus.model;

public class Corner {
	private Point point;

	private long firstStreetNameCode;

	private long secondStreetNameCode;

	private String firstStreetName;

	private String secondStreetName;

	public Corner() {
	}

	public Corner(Point point, long firstStreetNameCode,
			long secondStreetNameCode, String firstStreetName,
			String secondStreetName) {
		super();
		this.point = point;
		this.firstStreetNameCode = firstStreetNameCode;
		this.secondStreetNameCode = secondStreetNameCode;
		this.firstStreetName = firstStreetName;
		this.secondStreetName = secondStreetName;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public long getFirstStreetNameCode() {
		return firstStreetNameCode;
	}

	public void setFirstStreetNameCode(long firstStreetNameCode) {
		this.firstStreetNameCode = firstStreetNameCode;
	}

	public long getSecondStreetNameCode() {
		return secondStreetNameCode;
	}

	public void setSecondStreetNameCode(long secondStreetNameCode) {
		this.secondStreetNameCode = secondStreetNameCode;
	}

	public String getFirstStreetName() {
		return firstStreetName;
	}

	public void setFirstStreetName(String firstStreetName) {
		this.firstStreetName = firstStreetName;
	}

	public String getSecondStreetName() {
		return secondStreetName;
	}

	public void setSecondStreetName(String secondStreetName) {
		this.secondStreetName = secondStreetName;
	}

}
