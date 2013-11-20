package services.directions.bus;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.ShapefileWKT;
import model.dao.BusRouteMaximalDAO;
import model.dao.BusStopDAO;

import org.junit.Before;
import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class BusDirectionsImplTest {

	private BusDirectionsService busDirectionsService;
	private CoordinateConverter coordinateConverter;
	private BusStopDAO busStopDAO;
	private BusRouteMaximalDAO busRouteMaximalDAO;

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

		busDirectionsService = (BusDirectionsService) context
				.lookup("BusDirectionsServiceLocal");
		coordinateConverter = (CoordinateConverter) context
				.lookup("CoordinateConverterLocal");
		busRouteMaximalDAO = (BusRouteMaximalDAO) context
				.lookup("BusRouteMaximalDAOLocal");
		busStopDAO = (BusStopDAO) context.lookup("BusStopDAOLocal");

		// // add test data
		// GeometryFactory geometryFactory = new GeometryFactory();
		//
		// BusStop busStop = new BusStop();
		// busStop.setOrdinal(1);
		// busStop.setVariantCode(10);
		// Coordinate c = new Coordinate(-56.154924, -34.923942);
		// Point point = geometryFactory.createPoint(c);
		// busStop.setPoint(coordinateConverter.convertFromWGS84(point,
		// ShapefileWKT.BUS_STOP));
		// busStopDAO.add(busStop);
		//
		// busStop = new BusStop();
		// busStop.setOrdinal(2);
		// busStop.setVariantCode(10);
		// c = new Coordinate(-56.157371, -34.92271);
		// point = geometryFactory.createPoint(c);
		// busStop.setPoint(coordinateConverter.convertFromWGS84(point,
		// ShapefileWKT.BUS_STOP));
		// busStopDAO.add(busStop);
		//
		// busStop = new BusStop();
		// busStop.setOrdinal(3);
		// busStop.setVariantCode(10);
		// c = new Coordinate(-56.160997, -34.920018);
		// point = geometryFactory.createPoint(c);
		// busStop.setPoint(coordinateConverter.convertFromWGS84(point,
		// ShapefileWKT.BUS_STOP));
		// busStopDAO.add(busStop);
		//
		// busStop = new BusStop();
		// busStop.setOrdinal(4);
		// busStop.setVariantCode(10);
		// c = new Coordinate(-56.16252, -34.916939);
		// point = geometryFactory.createPoint(c);
		// busStop.setPoint(coordinateConverter.convertFromWGS84(point,
		// ShapefileWKT.BUS_STOP));
		// busStopDAO.add(busStop);
		//
		// busStop = new BusStop();
		// busStop.setOrdinal(5);
		// busStop.setVariantCode(10);
		// c = new Coordinate(-56.162778, -34.913332);
		// point = geometryFactory.createPoint(c);
		// busStop.setPoint(coordinateConverter.convertFromWGS84(point,
		// ShapefileWKT.BUS_STOP));
		// busStopDAO.add(busStop);
		//
		// BusRouteMaximal busRouteMaximal = new BusRouteMaximal();
		// busRouteMaximal.setLineName("línea 10");
		// busRouteMaximal.setSubLineDescription("Munoz y boulevard");
		// busRouteMaximal.setVariantCode(10);
		// Coordinate[] coords = new Coordinate[3];
		// coords[0] =
		// coordinateConverter.convertFromWGS84(geometryFactory.createPoint(new
		// Coordinate(-56.154088, -34.924223)),
		// ShapefileWKT.BUS_MAXIMAL).getCoordinate();
		// coords[1] =
		// coordinateConverter.convertFromWGS84(geometryFactory.createPoint(new
		// Coordinate(-56.159473, -34.921672)),
		// ShapefileWKT.BUS_MAXIMAL).getCoordinate();
		// coords[2] =
		// coordinateConverter.convertFromWGS84(geometryFactory.createPoint(new
		// Coordinate(-56.162048, -34.919314)),
		// ShapefileWKT.BUS_MAXIMAL).getCoordinate();
		// LineString l1 = geometryFactory.createLineString(coords);
	}

//	 @Test
//	 public void test() throws MismatchedDimensionException, FactoryException,
//	 TransformException {
//	 int distance = 500;
//	 GeometryFactory geo = new GeometryFactory();
//	 Coordinate c1 = new Coordinate();
//	 Coordinate c2 = new Coordinate();
//	 c1.y = -34.877341;
//	 c1.x = -56.057961;
//	 c2.y = -34.908352;
//	 c2.x = -56.186535;
//	 Point origin = coordinateConverter.convertFromWGS84(
//	 geo.createPoint(c1), ShapefileWKT.BUS_STOP);
//	 Point end = coordinateConverter.convertFromWGS84(geo.createPoint(c2),
//	 ShapefileWKT.BUS_STOP);
//	 List<BusRide> alternatives = busDirectionsService.getRoutes(origin,
//	 end, distance);
//	 for(BusRide alt: alternatives)
//	 System.out.println("line name: " + alt.getLineName() + " ; subLineCode: "
//	 + alt.getSubLineCode() + "; subLineDesc" + alt.getSubLineDescription() +
//	 "; variant code: " + alt.getStartStop().getVariantCode() +
//	 "; start stop pos code: " + alt.getStartStop().getBusStopLocationCode() +
//	 "; end stop pos code: " + alt.getEndStop().getBusStopLocationCode());
//	 assertTrue(true);
//	 }

//	@Test
//	public void test() throws MismatchedDimensionException, FactoryException,
//			TransformException {
//		int distance = 200;
//		GeometryFactory geo = new GeometryFactory();
//		Coordinate c1 = new Coordinate();
//		Coordinate c2 = new Coordinate();
//		c1.y = -34.904798;
//		c1.x = -56.183151;
//		c2.y = -34.893816;
//		c2.x = -56.150364;
//		Point origin = coordinateConverter.convertFromWGS84(
//				geo.createPoint(c1), ShapefileWKT.BUS_STOP);
//		Point end = coordinateConverter.convertFromWGS84(geo.createPoint(c2),
//				ShapefileWKT.BUS_STOP);
//		List<Transshipment> alternatives = busDirectionsService
//				.getRoutesWithTransshipment(origin, end, distance);
//		assertTrue(true);
//	}
}
