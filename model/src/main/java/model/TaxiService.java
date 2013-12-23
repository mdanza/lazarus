package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "taxis")
@NamedQueries({
		@NamedQuery(name = "TaxiService.findByName", query = "SELECT t FROM TaxiService t WHERE t.name = :name"),
		@NamedQuery(name = "TaxiService.findAll", query = "SELECT t FROM TaxiService t") })
public class TaxiService {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String phone;

	@Column(unique = true)
	private String name;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}
}
