package services.directions.bus.schedules;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class BusSchedulesServiceImplTest {

	private BusSchedulesService busSchedulesService;
	
	@Before
	public void configure() throws NamingException,
			MismatchedDimensionException, FactoryException, TransformException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.core.LocalInitialContextFactory");

		p.put("openejb.deployments.classpath.ear", "true");

		p.put("lazarus-persistence-unit", "new://Resource?type=DataSource");
		p.put("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
		p.put("lazarus-persistence-unit.JdbcUrl",
				"jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.Username", "postgres");
		p.put("lazarus-persistence-unit.Password", "postgres");
		// p.put("lazarus-persistence-unit.hibernate.hbm2ddl.auto",
		// "create-drop");
		p.put("lazarus-persistence-unit.hibernate.dialect",
				"org.hibernate.spatial.dialect.postgis.PostgisDialect");

		Context context = new InitialContext(p);

		busSchedulesService = (BusSchedulesService) context
				.lookup("BusSchedulesServiceLocal");
	}
	
//	@Test
//	public void test(){
//		List<String> times = busSchedulesService.getBusLineSchedule("64", "PLAZA INDEPENDENCIA - PUENTE CARRASCO", 2162, 0*60 + 30);
//		assertTrue(true);
//	}
}
