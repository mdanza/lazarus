package model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiLineString;

@Entity
@Table(name = "streets")
@NamedQueries({
	@NamedQuery(name = "Street.findByName", query = "SELECT s FROM Street s WHERE s.name = :name"),
	@NamedQuery(name = "Street.findByNameCode", query = "SELECT s FROM Street s WHERE s.nameCode = :nameCode")
})
public class Street {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String nameCode;
	
	@OneToMany
	@JoinColumn(name = "street_id")
	private List<StreetSegment> streetSegments;

	/*
	@Column
	@Type(type = "org.hibernatespatial.GeometryUserType")
	private MultiLineString segments;
	*/
	
	public Street() {
		super();
	}
	
	public Street(String name, String nameCode,
			List<StreetSegment> streetSegments) {
		super();
		this.name = name;
		this.nameCode = nameCode;
		this.streetSegments = streetSegments;
	}

	public void setId(int id) {
		this.id = id;
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

	public List<StreetSegment> getStreetSegments() {
		return streetSegments;
	}

	public void setStreetSegments(List<StreetSegment> streetSegments) {
		this.streetSegments = streetSegments;
	}

	public int getId() {
		return id;
	}
	
	

}