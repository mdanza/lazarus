package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "shapefiles_wkt")
@NamedQuery(name = "ShapefileWKT.findByType", query = "SELECT s FROM ShapefileWKT s WHERE s.shapefileType = :shapefileType")
public class ShapefileWKT {
	public static final String BUS_NON_MAXIMAL = "bus_non_maximal";
	public static final String BUS_MAXIMAL = "bus_maximal";
	public static final String BUS_STOP = "bus_stop";
	public static final String ADDRESS = "address";
	public static final String STREET = "street";
	public static final String CORNER = "corner";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(length=10000)
	private String wkt;

	@Column(unique = true)
	private String shapefileType;

	public String getWkt() {
		return wkt;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	public String getShapefileType() {
		return shapefileType;
	}

	public void setShapefileType(String shapefile) {
		if (shapefile == CORNER || shapefile == BUS_NON_MAXIMAL
				|| shapefile == CORNER || shapefile == BUS_MAXIMAL
				|| shapefile == BUS_STOP || shapefile == ADDRESS
				|| shapefile == STREET || shapefile == CORNER)
			this.shapefileType = shapefile;
	}

	public int getId() {
		return id;
	}
}
