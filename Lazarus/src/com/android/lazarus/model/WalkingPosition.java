package com.android.lazarus.model;

public class WalkingPosition {

	private Point point;

	private String instruction;

	public WalkingPosition() {
		super();
	}

	public WalkingPosition(Point point) {
		super();
		this.point = point;
	}

	public WalkingPosition(Point point, String instruction) {
		super();
		this.point = point;
		this.instruction = instruction;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	
}
