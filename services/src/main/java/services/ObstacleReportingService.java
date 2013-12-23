package services;

import helpers.RestResultsHelper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

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
				obstacleService.reportObstacle(point, intRadius, user,
						description);
				return restResultsHelper.resultWrapper(true,
						"added successfully");
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
	@Path("/{coordinates}")
	public String deactivateObstacle(
			@HeaderParam("Authorization") String token,
			@PathParam("coordinates") String coordinates) {
		if (token == null || token.equals("") || coordinates == null
				|| coordinates.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token or coordinates empty or null");
		try {
			User user = authenticationService.authenticate(token);
			Double x = Double.valueOf(coordinates.split(",")[0]);
			Double y = Double.valueOf(coordinates.split(",")[1]);
			Coordinate position = new Coordinate(x, y);
			GeometryFactory factory = new GeometryFactory();
			Point point = factory.createPoint(position);
			try {
				obstacleService.deactivateObstacle(point);
				return restResultsHelper.resultWrapper(true,
						"Successfully deactivated obstacle");
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"Could not deactivate obstacle");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}
}
