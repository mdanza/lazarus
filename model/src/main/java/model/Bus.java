package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "buses")
@NamedQuery(name = "Bus.findById", query = "SELECT b FROM Bus b WHERE b.id = :id")
public class Bus {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "variant_code")
	private int variantCode;

	@Column(name = "sub_line_code")
	private int subLineCode;

	@Column(name = "last_updated")
	private Date lastUpdated;

	@Column(name = "last_passed_stop_ordinal")
	private int lastPassedStopOrdinal;

	private double latitude;

	private double longitude;

	public int getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(int variantCode) {
		this.variantCode = variantCode;
	}

	public int getSubLineCode() {
		return subLineCode;
	}

	public void setSubLineCode(int subLineCode) {
		this.subLineCode = subLineCode;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public int getLastPassedStopOrdinal() {
		return lastPassedStopOrdinal;
	}

	public void setLastPassedStopOrdinal(int lastPassedStopOrdinal) {
		this.lastPassedStopOrdinal = lastPassedStopOrdinal;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getId() {
		return id;
	}

}
