package services.shapefiles;

import static org.junit.Assert.assertTrue;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import services.shapefiles.bus.BusStopLoaderImpl;

public class ShapefileLoaderTest {
	
	/*

	private static EJBContainer ejbContainer;

	private BusStopLoaderImpl busStopLoader;

	@BeforeClass
	public static void startTheContainer() {
		ejbContainer = EJBContainer.createEJBContainer();
	}

	@Before
	public void lookupABean() throws NamingException {
		Object object = ejbContainer.getContext().lookup(
				"java:global/model/BusStopLoader");

		assertTrue(object instanceof BusStopLoaderImpl);

		busStopLoader = (BusStopLoaderImpl) object;
	}

	@AfterClass
	public static void stopTheContainer() {
		if (ejbContainer != null) {
			ejbContainer.close();
		}
	}

	@Test
	public void printFieldsShp() throws Exception {
		// ShapefileLoader
		// .readShp("/home/santiago/Desktop/shapefile/uptu_variante_no_maximal.shp");
		// ShapefileLoader
		// .readShp("/home/santiago/Desktop/shapefile/v_uptu_lsv_destinos.shp");
		// ShapefileLoader
		// .readShp("/home/santiago/Desktop/shapefile/v_uptu_paradas.shp");
		busStopLoader
				.readShp("/home/santiago/Desktop/shapefile/v_uptu_paradas.shp");
	}
	*/
}
