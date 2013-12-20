package model;

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
@Table(name = "favourites")
@NamedQueries({
		@NamedQuery(name = "Favourite.findByUserAndName", query = "SELECT f FROM Favourite f WHERE f.user.username = :username AND f.name = :name"),
		@NamedQuery(name = "Favourite.findByUser", query = "SELECT f FROM Favourite f WHERE f.user.username = :username") })
public class Favourite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	@Column(nullable = false)
	private Point point;

	@Column(nullable = false)
	private String name;

	@ManyToOne
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
