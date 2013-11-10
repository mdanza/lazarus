package services.shapefiles.utils;

import static org.junit.Assert.assertTrue;

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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class CoordinateConverterTest {

	private CoordinateConverter coordinateConverter;

	@Before
	public void configure() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.core.LocalInitialContextFactory");

		p.put("openejb.deployments.classpath.ear", "true");

		p.put("lazarus-persistence-unit", "new://Resource?type=DataSource");
		p.put("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
		p.put("lazarus-persistence-unit.JdbcUrl", "jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.JdbcUrl", "jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.Username", "postgres");
		p.put("lazarus-persistence-unit.Password", "postgres");

		Context context = new InitialContext(p);

		coordinateConverter = (CoordinateConverter) context.lookup("CoordinateConverterLocal");
	}

	@Test
	public void test() throws MismatchedDimensionException, FactoryException, TransformException {
		GeometryFactory f = new GeometryFactory();
		//Coordinate c = new Coordinate(577776.31416, 6141077.73612);
		Coordinate c = new Coordinate(586114, 6140216);
		Point p = f.createPoint(c);
		Point converted = coordinateConverter.convertToWGS84(p, ShapefileWKT.BUS_STOP);
		
		Point reConverted = coordinateConverter.convertFromWGS84(converted, ShapefileWKT.BUS_STOP);
		
		assertTrue(p.getX() - reConverted.getX() < 0.001);
		assertTrue(p.getY() - reConverted.getY() < 0.001);
	}
	
}
