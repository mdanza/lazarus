package services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.Obstacle;
import model.ShapefileWKT;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.authentication.AuthenticationService;
import services.directions.bus.BusDirectionsService;
import services.directions.bus.BusDirectionsService.BusRide;
import services.directions.bus.BusDirectionsService.Transshipment;
import services.directions.walking.WalkingDirectionsService;
import services.directions.walking.WalkingPosition;
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

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@GET
	@Path("/walkingDirections")
	public String getWalkingDirections(@QueryParam("token") String token,
			@QueryParam("origin") String origin, @QueryParam("end") String end) {
		if (token == null || origin == null || end == null || token.equals("")
				|| origin.equals("") || end.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Double xOrigin = Double.valueOf(origin.split(",")[0]);
		Double yOrigin = Double.valueOf(origin.split(",")[1]);
		Double xEnd = Double.valueOf(end.split(",")[0]);
		Double yEnd = Double.valueOf(end.split(",")[1]);
		Coordinate originCoordinates = new Coordinate(Double.valueOf(xOrigin),
				Double.valueOf(yOrigin));
		Coordinate endCoordinates = new Coordinate(Double.valueOf(xEnd),
				Double.valueOf(yEnd));
		List<WalkingPosition> walkingDirections = walkingDirectionsService
				.getWalkingDirections(originCoordinates, endCoordinates);
		StringBuilder buildReturn = new StringBuilder();
		for (WalkingPosition walkingPosition : walkingDirections) {
			Coordinate coordinate = walkingPosition.getCoordinate();
			String instruction = walkingPosition.getInstruction();
			Obstacle obstacle = walkingPosition.getObstacle();
			buildReturn.append(coordinate.x + "," + coordinate.y);
			if (instruction != null) {
				buildReturn.append("," + instruction);
			} else if (obstacle != null) {
				buildReturn.append("," + "obstacle,radius:"
						+ obstacle.getRadius() + ",description:"
						+ obstacle.getDescription());
			}
			buildReturn.append(System.getProperty("line.separator"));
		}
		return buildReturn.toString();
	}

	@GET
	@Path("/busDirections")
	public String getBusDirections(@QueryParam("xOrigin") Double xOrigin,
			@QueryParam("yOrigin") Double yOrigin,
			@QueryParam("xEnd") Double xEnd, @QueryParam("yEnd") Double yEnd,
			@QueryParam("maxWalkingDistance") int distance,
			@QueryParam("token") String token)
			throws MismatchedDimensionException, FactoryException,
			TransformException {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null
				|| token == null || xOrigin.equals("") || yOrigin.equals("")
				|| xEnd.equals("") || xOrigin.equals("") || token.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
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
			@QueryParam("xOrigin") Double xOrigin,
			@QueryParam("yOrigin") Double yOrigin,
			@QueryParam("xEnd") Double xEnd, @QueryParam("yEnd") Double yEnd,
			@QueryParam("maxWalkingDistance") int distance,
			@QueryParam("token") String token)
			throws MismatchedDimensionException, FactoryException,
			TransformException {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null
				|| token == null || xOrigin.equals("") || yOrigin.equals("")
				|| xEnd.equals("") || xOrigin.equals("") || token.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Coordinate origin = new Coordinate(xOrigin, yOrigin);
		Coordinate end = new Coordinate(xEnd, yEnd);
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
