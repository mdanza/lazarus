package com.android.lazarus.model;

import java.util.List;

public class Street {
	private String name;

	private String nameCode;

	private List<Point> segments;

	public Street() {
		super();
	}

	public Street(String name, String nameCode, List<Point> segments) {
		super();
		this.name = name;
		this.nameCode = nameCode;
		this.segments = segments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameCode() {
		return nameCode;
	}

	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}

	public List<Point> getSegments() {
		return segments;
	}

	public void setSegments(List<Point> segments) {
		this.segments = segments;
	}

}