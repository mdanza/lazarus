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

import com.vividsolutions.jts.geom.MultiLineString;

@Entity
@Table(name = "streets")
@NamedQueries({
		@NamedQuery(name = "Street.findByName", query = "SELECT s FROM Street s WHERE s.name = :name"),
		@NamedQuery(name = "Street.findByNameCode", query = "SELECT s FROM Street s WHERE s.nameCode = :nameCode"),
		@NamedQuery(name = "Street.findPossibleStreets", query = "SELECT s.name FROM Street s WHERE s.name LIKE :name"),
		@NamedQuery(name = "Street.findClosestToPoint", query = "select s FROM Street s ORDER BY distance(:point, s.segments)")})
public class Street {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String nameCode;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private MultiLineString segments;

	public Street() {
		super();
	}

	public Street(String name, String nameCode, MultiLineString segments) {
		super();
		this.name = name;
		this.nameCode = nameCode;
		this.segments = segments;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public MultiLineString getSegments() {
		return segments;

	}

	public void setSegments(MultiLineString segments) {
		this.segments = segments;

	}

}