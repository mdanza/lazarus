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
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

@Entity
@Table(name = "obstacle")
@NamedQueries({ @NamedQuery(name = "Obstacle.findByCentre", query = "select o FROM Obstacle o WHERE o.centre = :centre") })
public class Obstacle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private Polygon circle;

	@Type(type = "org.hibernate.spatial.GeometryType")
	@Column(unique = true)
	private Point centre;

	private int radius;

	@ManyToOne
	private User user;

	private Date createdAt;

	public Obstacle() {
		this.createdAt = new Date();
	}

	public Obstacle(Point centre, int radius) {
		this.centre = centre;
		this.radius = radius;
		GeometricShapeFactory fac = new GeometricShapeFactory();
		fac.setSize(radius * 2.0);
		fac.setNumPoints(360);
		fac.setCentre(centre.getCoordinate());
		this.circle = fac.createCircle();
		this.createdAt = new Date();
	}

	public Obstacle(Point centre, int radius, User user) {
		this.centre = centre;
		this.radius = radius;
		this.user = user;
		GeometricShapeFactory fac = new GeometricShapeFactory();
		fac.setSize(radius * 2.0);
		fac.setNumPoints(360);
		fac.setCentre(centre.getCoordinate());
		this.circle = fac.createCircle();
		this.createdAt = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Polygon getCircle() {
		return circle;
	}

	public Point getCentre() {
		return centre;
	}

	public void setCentre(Point centre) {
		this.centre = centre;
		GeometricShapeFactory fac = new GeometricShapeFactory();
		fac.setSize(radius * 2.0);
		fac.setNumPoints(360);
		fac.setCentre(centre.getCoordinate());
		this.circle = fac.createCircle();
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
		GeometricShapeFactory fac = new GeometricShapeFactory();
		fac.setSize(radius * 2.0);
		fac.setNumPoints(360);
		fac.setCentre(centre.getCoordinate());
		this.circle = fac.createCircle();
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

}
