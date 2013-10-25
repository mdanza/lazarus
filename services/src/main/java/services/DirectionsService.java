package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import services.directions.walking.WalkingDirectionsService;

import com.vividsolutions.jts.geom.Coordinate;

@Stateless(name = "DirectionsService")
@Path("/api/directions")
public class DirectionsService {
	
	static Logger logger = Logger.getLogger(DirectionsService.class);

	@EJB(name = "WalkingDirectionsService")
	private WalkingDirectionsService walkingDirectionsService;

	@GET
	@Path("/walkingDirections")
	public String getWalkingDirections(@QueryParam("xOrigin") String xOrigin,@QueryParam("yOrigin") String yOrigin,
			@QueryParam("xEnd") String xEnd,@QueryParam("yEnd") String yEnd) {
		if (xOrigin == null || yOrigin == null || xEnd == null || yEnd == null || xOrigin.equals("") || yOrigin.equals("") || xEnd.equals("") || xOrigin.equals(""))
			throw new IllegalArgumentException("Empty or null arguments are not allowed");
		Coordinate origin = new Coordinate(Double.valueOf(xOrigin),Double.valueOf(yOrigin));
		Coordinate end = new Coordinate(Double.valueOf(xEnd),Double.valueOf(yEnd));
		walkingDirectionsService.getWalkingDirections(origin,end);
		return "Done";
	}
}
