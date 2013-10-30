package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.ShapefileWKT;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.directions.bus.BusDirectionsService;
import services.directions.walking.WalkingDirectionsService;
import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "DirectionsService")
@Path("/api/directions")
public class DirectionsService {
	
	private GeometryFactory geometryFactory = new GeometryFactory();

	static Logger logger = Logger.getLogger(DirectionsService.class);

	@EJB(name = "WalkingDirectionsService")
	private WalkingDirectionsService walkingDirectionsService;
	
	@EJB(name = "BusDirectionsService")
	private BusDirectionsService busDirectionsService;
	
	@EJB(name = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	@GET
	@Path("/walkingDirections")
	public String getWalkingDirections(@QueryParam("xOrigin") String xOrigin,
			@QueryParam("yOrigin") String yOrigin,
			@QueryParam("xEnd") String xEnd, @QueryParam("yEnd") String yEnd) {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null
				|| xOrigin.equals("") || yOrigin.equals("") || xEnd.equals("")
				|| xOrigin.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		Coordinate origin = new Coordinate(Double.valueOf(xOrigin),
				Double.valueOf(yOrigin));
		Coordinate end = new Coordinate(Double.valueOf(xEnd),
				Double.valueOf(yEnd));
		walkingDirectionsService.getWalkingDirections(origin, end);
		return "Done";
	}
	
	@GET
	@Path("/busDirections")
	public String getBusDirections(@QueryParam("xOrigin") String xOrigin,
			@QueryParam("yOrigin") String yOrigin,
			@QueryParam("xEnd") String xEnd, @QueryParam("yEnd") String yEnd, @QueryParam("maxWalkingDistance") int distance) throws MismatchedDimensionException, FactoryException, TransformException {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null
				|| xOrigin.equals("") || yOrigin.equals("") || xEnd.equals("")
				|| xOrigin.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		Coordinate origin = new Coordinate(Double.valueOf(xOrigin),
				Double.valueOf(yOrigin));
		Coordinate end = new Coordinate(Double.valueOf(xEnd),
				Double.valueOf(yEnd));
		Point originPoint = geometryFactory.createPoint(origin);
		Point endPoint = geometryFactory.createPoint(end);
		Point originConverted = coordinateConverter.convertFromWGS84(
				originPoint, ShapefileWKT.BUS_STOP);
		Point endConverted = coordinateConverter.convertFromWGS84(
				endPoint, ShapefileWKT.BUS_STOP);
		busDirectionsService.getRoutes(originConverted, endConverted, distance);
		return "Done";
	}
}
