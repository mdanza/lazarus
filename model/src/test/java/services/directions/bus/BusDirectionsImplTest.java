package services.directions.bus;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.ShapefileWKT;

import org.junit.Before;
import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.directions.bus.BusDirectionsService.BusRide;
import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class BusDirectionsImplTest {

	private BusDirectionsService busDirectionsService;
	private CoordinateConverter coordinateConverter;

	@Before
	public void configure() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.core.LocalInitialContextFactory");

		p.put("openejb.deployments.classpath.ear", "true");

		p.put("lazarus-persistence-unit", "new://Resource?type=DataSource");
		p.put("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
		p.put("lazarus-persistence-unit.JdbcUrl",
				"jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.JdbcUrl",
				"jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.Username", "postgres");
		p.put("lazarus-persistence-unit.Password", "postgres");

		Context context = new InitialContext(p);

		busDirectionsService = (BusDirectionsService) context
				.lookup("BusDirectionsServiceLocal");
		coordinateConverter = (CoordinateConverter) context
				.lookup("CoordinateConverterLocal");
	}

	@Test
	public void test() throws MismatchedDimensionException, FactoryException,
			TransformException {
		int distance = 100;
		GeometryFactory geo = new GeometryFactory();
		Coordinate c1 = new Coordinate();
		Coordinate c2 = new Coordinate();
		c1.y = -34.886776;
		c1.x = -56.137919;
		c2.y = -34.882903;
		c2.x = -56.083416;
		Point origin = coordinateConverter.convertFromWGS84(
				geo.createPoint(c1), ShapefileWKT.BUS_STOP);
		Point end = coordinateConverter.convertFromWGS84(geo.createPoint(c2),
				ShapefileWKT.BUS_STOP);
		List<BusRide> alternatives = busDirectionsService.getRoutes(origin, end,
				distance);
		assertTrue(true);
	}
}
