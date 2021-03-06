package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiLineString;

//Nombre 	        		Tipo 	    			Descripción
//gid	              | bigint                 | Código identificador, uso interno.				  
//cod_linea            | integer                | Código de línea de transporte
//desc_linea           | character varying(50)  | Descripción de la línea de transporte (ej: 145, D10, etc)
//ordinal_sublinea     | integer                | Número correlativo de la sublinea en la línea
//cod_sublinea         | integer                | Código sublínea de recorrido
//desc_sublinea        | character varying(50)  | Descripción de la sublínea de recorrido (ej: ADUANA-PORTONES)
//cod_variante         | integer                | Código de la variante de recorrido (para vincular con v_uptu_parada)
//desc_variante        | character(1)           | Descripción de la variante (Se asigna sentido A,
// a todas las líneas que circulan  con destino hacia la zona sur del departamento
//  (Ciudad Vieja, Centro, Cordón, P. Rodó, P. Carretas, Pocitos, Buceo, Malvín), además de todas las líneas 
//  que atraviesan esta zona en sentido Oeste-Este.
//  Se asigna sentido B, a todas las líneas en sentido contrario al antes mencionado.
//the_geom             | geometry               | Campo con la geometría
//cod_variante_maximal | integer                | Código variante máximal
//cod_origen           | integer                | Código origen de línea
//desc_origen          | character varying(100) | Descripción origen (inicio)
//cod_destino          | integer                | Código destino
//desc_destino         | character varying(100) | Descripción destino (fin recorrido)

@Entity
@Table(name = "bus_routes_maximal")
@NamedQuery(name = "BusRouteMaximal.removeAll", query = "DELETE FROM BusRouteMaximal")
public class BusRouteMaximal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "maximal_variant_code")
	private long maximalVariantCode;

	@Column(name = "variant_code")
	private long variantCode;

	@Column(name = "sub_line_code")
	private long subLineCode;

	@Column(name = "line_name")
	private String lineName;

	@Column(name = "sub_line_description")
	private String subLineDescription;

	@Column(name = "destination")
	private String destination;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private MultiLineString trajectory;

	public long getId() {
		return id;
	}

	public String getLineName() {
		return lineName;
	}

	public long getMaximalVariantCode() {
		return maximalVariantCode;
	}

	public void setMaximalVariantCode(long maximalVariantCode) {
		this.maximalVariantCode = maximalVariantCode;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public MultiLineString getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(MultiLineString trajectory) {
		this.trajectory = trajectory;
	}

	public long getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(long variantCode) {
		this.variantCode = variantCode;
	}

	public String getSubLineDescription() {
		return subLineDescription;
	}

	public void setSubLineDescription(String subLineDescription) {
		this.subLineDescription = subLineDescription;
	}

	public long getSubLineCode() {
		return subLineCode;
	}

	public void setSubLineCode(int subLineCode) {
		this.subLineCode = subLineCode;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
