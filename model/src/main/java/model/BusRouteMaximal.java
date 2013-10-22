package model;

import javax.persistence.Entity;
import javax.persistence.Id;
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
public class BusRouteMaximal {
	@Id
	private int id;

	private int maximalCode;

	private String lineName;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private MultiLineString trajectory;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLineName() {
		return lineName;
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

	public int getMaximalCode() {
		return maximalCode;
	}

	public void setMaximalCode(int maximalCode) {
		this.maximalCode = maximalCode;
	}

}
