package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

//Descripción del shape v_uptu_controles
//Autor: 	IM - UPTU
//Descripcion: Ubicaciones de puntos de control
// 
//Cod_ubic_c    Código de la ubicación de control
//Cod_varian    Código de la variante de línea de ómnibus (refiere v_uptu_lsv)
//Ordinal       Número correlativo del punto de control en el trayecto de la variante
//Calle         Nombre de la calle sobre la que se ubica la parada
//Cod_calle1    Código de la Calle según nomenclator oficial de Montevideo
//Esquina       Nombre de la esquina más próxima 
//Cod_calle1    Código de la Esquina según nomenclator oficial de Montevideo
//X             Coordenada X de la ubicación (SIRGAS2000 UTM 21s)
//Y             Coordenada Y de la ubicación (SIRGAS2000 UTM 21s)

@Entity
@Table(name = "control_points")
@NamedQuery(name = "ControlPoint.findById", query = "SELECT c FROM ControlPoint c WHERE c.id = :id")
public class ControlPoint {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "control_point_location_code")
	private int controlPointLocationCode;

	@Column(name = "variant_code")
	private int variantCode;

	private int ordinal;

	@Column(name = "street_code")
	private long streetCode;

	@Column(name = "corner_street_code")
	private long cornerStreetCode;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point point;

	@Column(name = "location_description")
	private String locationDescription;

	public int getId() {
		return id;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getControlPointLocationCode() {
		return controlPointLocationCode;
	}

	public void setControlPointLocationCode(int controlPointLocationCode) {
		this.controlPointLocationCode = controlPointLocationCode;
	}

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

	public long getStreetCode() {
		return streetCode;
	}

	public void setStreetCode(long streetCode) {
		this.streetCode = streetCode;
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
}