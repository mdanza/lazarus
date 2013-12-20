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
	private long id;

	@Column(name = "variant_code")
	private long variantCode;

	@Column(name = "sub_line_code")
	private long subLineCode;

	@Column(name = "last_updated")
	private Date lastUpdated;

	@Column(name = "last_passed_stop_ordinal")
	private long lastPassedStopOrdinal;

	private double latitude;

	private double longitude;

	public long getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(long variantCode) {
		this.variantCode = variantCode;
	}

	public long getSubLineCode() {
		return subLineCode;
	}

	public void setSubLineCode(long subLineCode) {
		this.subLineCode = subLineCode;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public long getLastPassedStopOrdinal() {
		return lastPassedStopOrdinal;
	}

	public void setLastPassedStopOrdinal(long lastPassedStopOrdinal) {
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

	public long getId() {
		return id;
	}

}
