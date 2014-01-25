package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "route_types")
@NamedQueries({
		@NamedQuery(name = "RouteType.findAll", query = "SELECT r FROM RouteType r"),
		@NamedQuery(name = "RouteType.removeAll", query = "DELETE FROM RouteType") })
public class RouteType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String description;

	private String abbreviateDescription;

	public RouteType(String description, String abbreviateDescription) {
		this.description = description;
		this.abbreviateDescription = abbreviateDescription;
	}

	public RouteType() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAbbreviateDescription() {
		return abbreviateDescription;
	}

	public void setAbbreviateDescription(String abbreviateDescription) {
		this.abbreviateDescription = abbreviateDescription;
	}

	public long getId() {
		return id;
	}

}
