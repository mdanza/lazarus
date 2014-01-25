package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "route_titles")
@NamedQueries({
		@NamedQuery(name = "RouteTitle.findAll", query = "SELECT r FROM RouteTitle r"),
		@NamedQuery(name = "RouteTitle.removeAll", query = "DELETE FROM RouteTitle") })
public class RouteTitle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String description;

	private String abbreviateDescription;

	public RouteTitle() {
	}
	
	public RouteTitle(String description, String abbreviateDescription) {
		this.description = description;
		this.abbreviateDescription = abbreviateDescription;
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
