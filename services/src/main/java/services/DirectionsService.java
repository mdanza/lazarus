package services;

import helpers.RestResultsHelper;

import java.lang.reflect.Type;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.ShapefileWKT;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.authentication.AuthenticationService;
import services.directions.bus.BusDirectionsService;
import services.directions.bus.BusExclusionStrategy;
import services.directions.bus.BusRide;
import services.directions.bus.Transshipment;
import services.shapefiles.utils.CoordinateConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "DirectionsService")
@Path("v1/api/directions")
public class DirectionsService {

	private GeometryFactory geometryFactory = new GeometryFactory();

	private Gson gson = createGson();

	static Logger logger = Logger.getLogger(DirectionsService.class);

	@EJB(name = "BusDirectionsService")
	private BusDirectionsService busDirectionsService;

	@EJB(name = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new BusExclusionStrategy());
		return builder.create();
	}

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@GET
	@Path("/busDirections")
	public String getBusDirections(@QueryParam("xOrigin") Double xOrigin,
			@QueryParam("yOrigin") Double yOrigin,
			@QueryParam("xEnd") Double xEnd, @QueryParam("yEnd") Double yEnd,
			@QueryParam("maxWalkingDistance") int distance,
			@QueryParam("pageNumber") Integer pageNumber,
			@HeaderParam("Authorization") String token)
			throws MismatchedDimensionException, FactoryException,
			TransformException {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null
				|| token == null || xOrigin.equals("") || yOrigin.equals("")
				|| xEnd.equals("") || xOrigin.equals("") || token.equals("")
				|| pageNumber == null || pageNumber.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			Coordinate origin = new Coordinate(xOrigin, yOrigin);
			Coordinate end = new Coordinate(xEnd, yEnd);
			Point originPoint = geometryFactory.createPoint(origin);
			Point endPoint = geometryFactory.createPoint(end);
			Point originConverted = coordinateConverter.convertFromWGS84(
					originPoint, ShapefileWKT.BUS_STOP);
			Point endConverted = coordinateConverter.convertFromWGS84(endPoint,
					ShapefileWKT.BUS_STOP);
			try {
				List<BusRide> results = busDirectionsService.getRoutes(
						originConverted, endConverted, distance, pageNumber);
				if (results != null) {
					Type type = new TypeToken<List<BusRide>>() {
					}.getType();
					String serializedDirections = gson.toJson(results, type);
					return restResultsHelper.resultWrapper(true,
							serializedDirections);
				} else
					return restResultsHelper.resultWrapper(false,
							"No directions available");
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"Could not get directions");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	@Path("/busDirectionsWithTransshipment")
	public String getBusDirectionsWithTransshipment(
			@QueryParam("xOrigin") Double xOrigin,
			@QueryParam("yOrigin") Double yOrigin,
			@QueryParam("xEnd") Double xEnd, @QueryParam("yEnd") Double yEnd,
			@QueryParam("maxWalkingDistance") int distance,
			@QueryParam("pageNumber") Integer pageNumber,
			@HeaderParam("Authorization") String token)
			throws MismatchedDimensionException, FactoryException,
			TransformException {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null
				|| token == null || xOrigin.equals("") || yOrigin.equals("")
				|| xEnd.equals("") || xOrigin.equals("") || token.equals("")
				|| pageNumber == null)
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			Coordinate origin = new Coordinate(xOrigin, yOrigin);
			Coordinate end = new Coordinate(xEnd, yEnd);
			Point originPoint = geometryFactory.createPoint(origin);
			Point endPoint = geometryFactory.createPoint(end);
			Point originConverted = coordinateConverter.convertFromWGS84(
					originPoint, ShapefileWKT.BUS_STOP);
			Point endConverted = coordinateConverter.convertFromWGS84(endPoint,
					ShapefileWKT.BUS_STOP);
			try {
				List<Transshipment> results = busDirectionsService
						.getRoutesWithTransshipment(originConverted,
								endConverted, distance, pageNumber);
				if (results != null) {
					Type type = new TypeToken<List<Transshipment>>() {
					}.getType();
					String serializedDirections = gson.toJson(results, type);
					return restResultsHelper.resultWrapper(true,
							serializedDirections);
				} else
					return restResultsHelper.resultWrapper(false,
							"No directions available");
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"Could not get directions");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}
}
