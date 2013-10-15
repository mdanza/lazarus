package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	private int streetCode;

	private String cornerStreetName;

	private int cornerStreetCode;

	// @ManyToOne
	// @JoinColumn(name = "position_id")
	// private Position position;

	private double x;
	private double y;

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

	public int getStreetCode() {
		return streetCode;
	}

	public void setStreetCode(int streetCode) {
		this.streetCode = streetCode;
	}

	public String getCornerStreetName() {
		return cornerStreetName;
	}

	public void setCornerStreetName(String cornerStreetName) {
		this.cornerStreetName = cornerStreetName;
	}

	public int getCornerStreetCode() {
		return cornerStreetCode;
	}

	public void setCornerStreetCode(int cornerStreetCode) {
		this.cornerStreetCode = cornerStreetCode;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}
}
