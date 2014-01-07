package services.shapefiles;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.shapefiles.ShapefileStatusService.ShapefileStatus;

public class ShapefileStatusServiceTest {

//	private ShapefileStatusService shapefileStatusService;
//
//	@Before
//	public void configure() throws NamingException,
//			MismatchedDimensionException, FactoryException, TransformException {
//		Properties p = new Properties();
//		p.put(Context.INITIAL_CONTEXT_FACTORY,
//				"org.apache.openejb.core.LocalInitialContextFactory");
//
//		p.put("openejb.deployments.classpath.ear", "true");
//
//		p.put("lazarus-persistence-unit", "new://Resource?type=DataSource");
//		p.put("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
//		p.put("lazarus-persistence-unit.JdbcUrl",
//				"jdbc:postgresql://localhost/lazarus");
//		p.put("lazarus-persistence-unit.Username", "santiago");
//		p.put("lazarus-persistence-unit.Password", "");
//		p.put("lazarus-persistence-unit.hibernate.dialect",
//				"org.hibernate.spatial.dialect.postgis.PostgisDialect");
//		Context context = new InitialContext(p);
//		shapefileStatusService = (ShapefileStatusService) context
//				.lookup("ShapefileStatusServiceLocal");
//	}
//
//	@Test
//	public void getStatusTest() {
//		ShapefileStatus status = shapefileStatusService.getUploadStatus();
//		assertTrue(status != null);
//	}

}
