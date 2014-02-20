package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name = "obstacles")
@NamedQueries({
		@NamedQuery(name = "Obstacle.findByCentre", query = "select o FROM Obstacle o WHERE o.centre = :centre"),
		@NamedQuery(name = "Obstacle.findAll", query = "select o FROM Obstacle o"),
		@NamedQuery(name = "Obstacle.findById", query = "SELECT o FROM Obstacle o WHERE o.id = :id") })
public class Obstacle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	@Column(unique = true)
	private Point centre;

	private long radius;

	@ManyToOne
	private User user;

	private Date createdAt;

	private String description;

	public Obstacle() {
		this.createdAt = new Date();
	}

	public Obstacle(Point centre, long radius) {
		this.centre = centre;
		this.radius = radius;
		this.createdAt = new Date();
	}

	public Obstacle(Point centre, long radius, User user, String description) {
		this.centre = centre;
		this.radius = radius;
		this.user = user;
		this.createdAt = new Date();
		this.description = description;
	}

	public Obstacle(long id, Point centre, long radius, User user,
			String description) {
		this.centre = centre;
		this.radius = radius;
		this.user = user;
		this.createdAt = new Date();
		this.description = description;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
