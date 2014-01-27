package services.shapefiles;

import java.io.File;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import services.shapefiles.bus.BusRoutesMaximalLoader;
import services.shapefiles.bus.BusRoutesMaximalLoaderImpl;

public class ShapefileLoaderTest {
	
	
//
//	private static EJBContainer ejbContainer;
//
//	private BusRoutesMaximalLoader busRoutesMaximalLoader;
//
//	@BeforeClass
//	public static void startTheContainer() {
//		Properties p = new Properties();
//		p.setProperty("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
//		p.setProperty("lazarus-persistence-unit.JdbcUrl", " jdbc:postgresql://localhost/lazarus");
//		p.setProperty("lazarus-persistence-unit.UserName", "lazarus");
//		p.setProperty("lazarus-persistence-unit.Password", "");		
//		ejbContainer = EJBContainer.createEJBContainer(p);
//	}
//
//	@Before
//	public void lookupABean() throws NamingException {
//		Object object = ejbContainer.getContext().lookup(
//				"java:global/model/BusRoutesMaximalLoader");
//
//		busRoutesMaximalLoader = (BusRoutesMaximalLoader) object;
//	}
//
//	@AfterClass
//	public static void stopTheContainer() {
//		if (ejbContainer != null) {
//			ejbContainer.close();
//		}
//	}

//	@Test
//	public void printFieldsShp() throws Exception {
//		BusRoutesMaximalLoader busRoutesMaximalLoader = new BusRoutesMaximalLoaderImpl();
//		busRoutesMaximalLoader.updateShp(new File("/home/santiago/Downloads/v_uptu_lsv_destinos/v_uptu_lsv_destinos.shp"));
//		
//		 ShapefileLoader
//		 .readShp("/home/santiago/Desktop/shapefile/uptu_variante_no_maximal.shp")
//		 ShapefileLoader
//		 .readShp("/home/santiago/Desktop/shapefile/v_uptu_paradas.shp");
//	}
	
}
