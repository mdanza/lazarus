package model;

import javax.persistence.Column;
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
@Table(name = "addresses")
@NamedQueries({ @NamedQuery(name = "Address.findByStreetNameAndNumber", query = "SELECT a FROM Address a WHERE a.streetName = :streetName AND a.number = :number AND a.letter = :letter") })
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point point;

	private long padron;

	private long nameCode;

	@Column(nullable = false)
	private String streetName;

	@Column(nullable = false)
	private int number;

	private String letter;

	private String paridad;

	public Address() {
		super();
	}

	public Address(Point point, long padron, long nameCode, String streetName,
			int number, String letter, String paridad) {
		super();
		this.point = point;
		this.padron = padron;
		this.nameCode = nameCode;
		this.streetName = streetName;
		this.number = number;
		this.letter = letter;
		this.paridad = paridad;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
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
