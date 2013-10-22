package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiLineString;

//cod_varian	Código de la variante no maximal (para vincular con los horarios de ómnibus)
//cod_var_01	Código de la variante maximal correspondiente (para vincular con v_uptu_parada)
//desc_varia  	Descripción de la variante (Se asigna sentido A, a todas las líneas que circulan 
//            	con destino hacia la zona sur del departamento (Ciudad Vieja, Centro, Cordón, 
//            	P. Rodó, P. Carretas, Pocitos, Buceo, Malvín), además de todas las líneas 
//            	que atraviesan esta zona en sentido Oeste-Este.
//            	Se asigna sentido B, a todas las líneas en sentido contrario al antes mencionado.	
//		NOTA: esta descripción coincide con la descripción de la variante maximal de esta linea.
//cod_ubic_p	Cógido de ubicación de la parada origen, donde comienza la variante no maximal (para vincular con v_uptu_parada)
//cod_ubi_01	Cógido de ubicación de la parada destino, donde finaliza la variante no maximal (para vincular con v_uptu_parada)
//ordinal_or	Ordinal de la parada de origen
//ordinal_de	Ordinal de la parada de destino

@Entity
@Table(name = "bus_routes_non_maximal")
public class BusRouteNonMaximal {
	@Id
	private int id;

	@Type(type = "org.hibernate.spatial.GeometryType")
	private MultiLineString trajectory;

	private String description;

	private int variantCode;

	private int originBusStopCode;

	private int endBusStopCode;

	private int originBusStopOrdinal;

	private int endBusStopOrdinal;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVariantCode() {
		return variantCode;
	}

	public void setVariantCode(int variantCode) {
		this.variantCode = variantCode;
	}

	public int getOriginBusStopCode() {
		return originBusStopCode;
	}

	public void setOriginBusStopCode(int originBusStopCode) {
		this.originBusStopCode = originBusStopCode;
	}

	public int getEndBusStopCode() {
		return endBusStopCode;
	}

	public void setEndBusStopCode(int endBusStopCode) {
		this.endBusStopCode = endBusStopCode;
	}

	public int getOriginBusStopOrdinal() {
		return originBusStopOrdinal;
	}

	public void setOriginBusStopOrdinal(int originBusStopOrdinal) {
		this.originBusStopOrdinal = originBusStopOrdinal;
	}

	public int getEndBusStopOrdinal() {
		return endBusStopOrdinal;
	}

	public void setEndBusStopOrdinal(int endBusStopOrdinal) {
		this.endBusStopOrdinal = endBusStopOrdinal;
	}

	public MultiLineString getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(MultiLineString trajectory) {
		this.trajectory = trajectory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
