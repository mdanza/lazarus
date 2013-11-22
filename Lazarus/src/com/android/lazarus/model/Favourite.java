package com.android.lazarus.model;

import com.android.lazarus.model.User;

public class Favourite {

	private Point point;

	private String name;

	private User user;

	public Favourite() {
		super();
	}

	public Favourite(Point point, String name, User user) {
		super();
		this.point = point;
		this.name = name;
		this.user = user;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
