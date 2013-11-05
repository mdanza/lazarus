package services;

import java.util.List;

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
import services.directions.bus.BusDirectionsService.BusRide;
import services.directions.bus.BusDirectionsService.Transshipment;
import services.directions.walking.WalkingDirectionsService;
import services.shapefiles.utils.CoordinateConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "DirectionsService")
@Path("/api/directions")
public class DirectionsService {

	private GeometryFactory geometryFactory = new GeometryFactory();

	private Gson gson = createGson();

	static Logger logger = Logger.getLogger(DirectionsService.class);

	@EJB(name = "WalkingDirectionsService")
	private WalkingDirectionsService walkingDirectionsService;

	@EJB(name = "BusDirectionsService")
	private BusDirectionsService busDirectionsService;

	@EJB(name = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		return builder.create();
	}

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
	public String getBusDirections(@QueryParam("xOrigin") Double xOrigin,
			@QueryParam("yOrigin") Double yOrigin,
			@QueryParam("xEnd") Double xEnd, @QueryParam("yEnd") Double yEnd,
			@QueryParam("maxWalkingDistance") int distance)
			throws MismatchedDimensionException, FactoryException,
			TransformException {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null
				|| xOrigin.equals("") || yOrigin.equals("") || xEnd.equals("")
				|| xOrigin.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		Coordinate origin = new Coordinate(xOrigin, yOrigin);
		Coordinate end = new Coordinate(xEnd, yEnd);
		Point originPoint = geometryFactory.createPoint(origin);
		Point endPoint = geometryFactory.createPoint(end);
		Point originConverted = coordinateConverter.convertFromWGS84(
				originPoint, ShapefileWKT.BUS_STOP);
		Point endConverted = coordinateConverter.convertFromWGS84(endPoint,
				ShapefileWKT.BUS_STOP);
		List<BusRide> results = busDirectionsService.getRoutes(originConverted,
				endConverted, distance);
		return gson.toJson(results);
	}

	@GET
	@Path("/busDirectionsWithTransshipment")
	public String getBusDirectionsWithTransshipment(
			@QueryParam("xOrigin") String xOrigin,
			@QueryParam("yOrigin") String yOrigin,
			@QueryParam("xEnd") String xEnd, @QueryParam("yEnd") String yEnd,
			@QueryParam("maxWalkingDistance") int distance)
			throws MismatchedDimensionException, FactoryException,
			TransformException {
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
		Point endConverted = coordinateConverter.convertFromWGS84(endPoint,
				ShapefileWKT.BUS_STOP);
		List<Transshipment> results = busDirectionsService
				.getRoutesWithTransshipment(originConverted, endConverted,
						distance);
		return gson.toJson(results);
	}
}
