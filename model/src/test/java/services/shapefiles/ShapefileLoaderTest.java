package services.shapefiles;

import org.junit.Test;

public class ShapefileLoaderTest {

	@Test
	public void printFieldsShp() throws Exception {
		ShapefileLoader
				.readShp("/home/santiago/Desktop/shapefile/uptu_variante_no_maximal.shp");
		ShapefileLoader
				.readShp("/home/santiago/Desktop/shapefile/v_uptu_lsv_destinos.shp");
		ShapefileLoader
				.readShp("/home/santiago/Desktop/shapefile/v_uptu_paradas.shp");
	}
}
