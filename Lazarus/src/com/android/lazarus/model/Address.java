package com.android.lazarus.model;

public class Address {

	private Point point;

	private long padron;

	private long nameCode;

	private String streetName;

	private long number;

	private String letter;

	private String paridad;

	public Address() {
		super();
	}

	public Address(Point point, long padron, long nameCode, String streetName,
			long number, String letter, String paridad) {
		super();
		this.point = point;
		this.padron = padron;
		this.nameCode = nameCode;
		this.streetName = streetName;
		this.number = number;
		this.letter = letter;
		this.paridad = paridad;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public long getPadron() {
		return padron;
	}

	public void setPadron(long padron) {
		this.padron = padron;
	}

	public long getNameCode() {
		return nameCode;
	}

	public void setNameCode(long nameCode) {
		this.nameCode = nameCode;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public String getParidad() {
		return paridad;
	}

	public void setParidad(String paridad) {
		this.paridad = paridad;
	}

}
