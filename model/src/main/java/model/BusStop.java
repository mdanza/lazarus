package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class BusStop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int variantCode;

	private int ordinal;

	private String streetName;

	private long streetCode;

	private String cornerStreetName;

	private long cornerStreetCode;

	@Column(columnDefinition = "Geometry", nullable = true)
	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point point;

	public int getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(int variantCode) {
		this.variantCode = variantCode;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
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
}
