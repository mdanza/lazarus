package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name = "corners")
@NamedQueries({
		@NamedQuery(name = "Corner.findWithinRadius", query = "select c FROM Corner c WHERE dwithin(c.point, :point, :radius) = true"),
		@NamedQuery(name = "Corner.findClosestToPoint", query = "select c FROM Corner c ORDER BY distance(:point, c.point)"),
		@NamedQuery(name = "Corner.findByStreetNames", query = "select c FROM Corner c WHERE c.firstStreetName = :firstStreetName AND c.secondStreetName = :secondStreetName") })
public class Corner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Type(type = "org.hibernate.spatial.GeometryType")
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
