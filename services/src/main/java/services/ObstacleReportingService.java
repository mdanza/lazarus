package services;

import helpers.RestResultsHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import model.Obstacle;
import model.ShapefileWKT;
import model.User;
import serialization.ObstacleSerializer;
import services.authentication.AuthenticationService;
import services.incidents.obstacles.ObstacleService;
import services.shapefiles.utils.CoordinateConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "ObstacleReportingService")
@Path("v1/api/obstacles")
public class ObstacleReportingService {

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "ObstacleService")
	private ObstacleService obstacleService;

	@EJB(name = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	private Gson gson = createGson();

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Obstacle.class, new ObstacleSerializer());
		return builder.create();
	}

	@GET
	public String getObstacles(@HeaderParam("Authorization") String token) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false, "Empty or nul token");
		try {
			User user = authenticationService.authenticate(token);
			try {
				List<Obstacle> obstacles = obstacleService.getAll();
				Type type = new TypeToken<List<Obstacle>>() {
				}.getType();
				if (obstacles != null) {
					List<Obstacle> result = new ArrayList<Obstacle>();
					for (Obstacle o : obstacles) {
						Obstacle copy = new Obstacle(o.getId(), o.getCentre(),
								o.getRadius(), o.getUser(), o.getDescription());
						copy.setCentre(coordinateConverter.convertToWGS84(
								copy.getCentre(), ShapefileWKT.OBSTACLE));
						result.add(copy);
					}
					return restResultsHelper.resultWrapper(true,
							gson.toJson(result, type));
				} else
					return restResultsHelper.resultWrapper(true,
							gson.toJson(new ArrayList<Obstacle>(), type));
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"could not get obstacles");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@POST
	public String reportObstacle(@HeaderParam("Authorization") String token,
			@FormParam("coordinates") String coordinates,
			@FormParam("radius") String radius,
			@FormParam("description") String description) {
		if (token == null || token.equals("") || coordinates == null
				|| coordinates.equals("") || radius == null
				|| radius.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token, coordinates or radius empty or null");
		try {
			User user = authenticationService.authenticate(token);
			Double x = Double.valueOf(coordinates.split(",")[0]);
			Double y = Double.valueOf(coordinates.split(",")[1]);
			Coordinate position = new Coordinate(x, y);
			long intRadius = Long.valueOf(radius);
			GeometryFactory factory = new GeometryFactory();
			Point point = factory.createPoint(position);
			try {
				long id = obstacleService.reportObstacle(point, intRadius, user,
						description);
				return restResultsHelper.resultWrapper(true,
						String.valueOf(id));
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"could not add obstacle");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@DELETE
	@Path("/{id}")
	public String deactivateObstacle(
			@HeaderParam("Authorization") String token,
			@PathParam("id") String id) {
		if (token == null || token.equals("") || id == null
				|| id.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token or id empty or null");
		try {
			User user = authenticationService.authenticate(token);
			try {
				long longId = Long.parseLong(id);
				obstacleService.deleteObstacle(longId);
				return restResultsHelper.resultWrapper(true,
						"Successfully deactivated obstacle");
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"Invalid id");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}
}
