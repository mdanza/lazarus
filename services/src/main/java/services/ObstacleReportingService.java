package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import model.User;
import services.authentication.AuthenticationService;
import services.incidents.obstacles.ObstacleService;

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

	@POST
	public String reportObstacle(@HeaderParam("Authorization") String token,
			@FormParam("coordinates") String coordinates,
			@FormParam("radius") String radius,
			@FormParam("description") String description) {
		if (token == null || token.equals("") || coordinates == null
				|| coordinates.equals("") || radius == null
				|| radius.equals(""))
			throw new IllegalArgumentException(
					"Token, coordinates or radius empty or null");
		User user = authenticationService.authenticate(token);
		Double x = Double.valueOf(coordinates.split(",")[0]);
		Double y = Double.valueOf(coordinates.split(",")[1]);
		Coordinate position = new Coordinate(x, y);
		int intRadius = Integer.valueOf(radius);
		GeometryFactory factory = new GeometryFactory();
		Point point = factory.createPoint(position);
		obstacleService.reportObstacle(point, intRadius, user, description);
		return "Done";
	}

	@DELETE
	public String deactivateObstacle(
			@HeaderParam("Authorization") String token,
			@FormParam("coordinates") String coordinates) {
		if (token == null || token.equals("") || coordinates == null
				|| coordinates.equals(""))
			throw new IllegalArgumentException(
					"Token or coordinates empty or null");
		User user = authenticationService.authenticate(token);
		Double x = Double.valueOf(coordinates.split(",")[0]);
		Double y = Double.valueOf(coordinates.split(",")[1]);
		Coordinate position = new Coordinate(x, y);
		GeometryFactory factory = new GeometryFactory();
		Point point = factory.createPoint(position);
		obstacleService.deactivateObstacle(point);
		return "Done";
	}
}
