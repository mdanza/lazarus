package com.android.lazarus.model;

import java.util.Date;

public class Obstacle {

	private Point centre;

	private long radius;

	private User user;

	private Date createdAt;

	private String description;

	private long id;

	public Obstacle() {
		this.createdAt = new Date();
	}

	public Obstacle(Point centre, int radius) {
		this.centre = centre;
		this.radius = radius;
		this.createdAt = new Date();
	}

	public Obstacle(Point centre, int radius, User user, String description) {
		this.centre = centre;
		this.radius = radius;
		this.user = user;
		this.createdAt = new Date();
		this.description = description;
	}

	public Point getCentre() {
		return centre;
	}

	public void setCentre(Point centre) {
		this.centre = centre;
	}

	public long getRadius() {
		return radius;
	}

	public void setRadius(long radius) {
		this.radius = radius;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(long id) {
		this.id = id;
		
	}

}
