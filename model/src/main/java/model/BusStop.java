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

//Cod_ubic_p    Código de la ubicación de parada
//Cod_varian    Código de la variante de línea de ómnibus (refiere v_uptu_lsv)
//Ordinal       Número correlativo de la parada en el trayecto de la variante
//Calle         Nombre de la calle sobre la que se ubica la parada
//Cod_calle1    Código de la Calle según nomenclator oficial de Montevideo
//Esquina       Nombre de la esquina más próxima 
//Cod_calle1    Código de la Esquina según nomenclator oficial de Montevideo
//X             Coordenada X de la ubicación (SIRGAS2000 UTM 21s)
//Y             Coordenada Y de la ubicación (SIRGAS2000 UTM 21s)
@Entity
@Table(name = "bus_stops")
@NamedQueries({
		@NamedQuery(name = "BusStop.findById", query = "SELECT b FROM BusStop b WHERE b.id = :id"), 
		@NamedQuery(name = "BusStop.findByOrdinalFromSameLine", query = "SELECT stop FROM BusStop stop WHERE stop.active = true AND stop.variantCode = :variantCode AND stop.ordinal = :ordinal")})
public class BusStop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "bus_stop_location_code")
	private int busStopLocationCode;

	@Column(name = "variant_code")
	private int variantCode;

	private int ordinal;

	@Column(name = "street_name")
	private String streetName;

	@Column(name = "street_code")
	private long streetCode;

	@Column(name = "corner_street_name")
	private String cornerStreetName;

	@Column(name = "corner_street_code")
	private long cornerStreetCode;
	
	private boolean active;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point point;

	public BusStop(){
		this.active = true;
	}
	
	public BusStop(BusStop anotherBusStop){
		this.busStopLocationCode = anotherBusStop.busStopLocationCode;
		this.variantCode = anotherBusStop.variantCode;
		this.ordinal = anotherBusStop.ordinal;
		this.streetName = anotherBusStop.streetName;
		this.streetCode = anotherBusStop.streetCode;
		this.cornerStreetName = anotherBusStop.cornerStreetName;
		this.cornerStreetCode = anotherBusStop.cornerStreetCode;
		this.point = anotherBusStop.point;
		this.active = anotherBusStop.active;
	}
	
	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getBusStopLocationCode() {
		return busStopLocationCode;
	}

	public void setBusStopLocationCode(int busStopLocationCode) {
		this.busStopLocationCode = busStopLocationCode;
	}

	public int getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(int variantCode) {
		this.variantCode = variantCode;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public long getStreetCode() {
		return streetCode;
	}

	public void setStreetCode(long sreetCode) {
		this.streetCode = sreetCode;
	}

	public String getCornerStreetName() {
		return cornerStreetName;
	}

	public void setCornerStreetName(String cornerStreetName) {
		this.cornerStreetName = cornerStreetName;
	}

	public long getCornerStreetCode() {
		return cornerStreetCode;
	}

	public void setCornerStreetCode(long cornerStreetCode) {
		this.cornerStreetCode = cornerStreetCode;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public int getId() {
		return id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
