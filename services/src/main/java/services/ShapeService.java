package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.BusStop;
import model.dao.BusStopDAO;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;
import services.shapefiles.bus.BusStopLoader;
import services.shapefiles.streets.StreetLoader;

@Stateless(name = "ShapeService")
@Path("/api/shapes")
public class ShapeService {

	static Logger logger = Logger.getLogger(UserService.class);

	@EJB(name = "StreetLoader")
	private StreetLoader streetLoader;
	
	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "BusStopLoader")
	private BusStopLoader busStopLoader;
	
	@EJB(name = "BusStopDAO")
	private BusStopDAO busStopDAO;
	
	@POST
	@Path("/uploadStreets")
	public String uploadStreets(@QueryParam("url") String url) {
		if(url==null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		streetLoader.readShp(url);
		return "Done";
	}
	
	@POST
	@Path("/uploadBusStops")
	public String busStops(@QueryParam("url") String url){
		if(url==null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		busStopLoader.readShp(url);
		return "Done";
	}
	
	@GET
	@Path("/getBusStop")
	public String getBusStop(@QueryParam("id") int id){
		BusStop busStop = busStopDAO.find(id); 
		return  busStop.getPoint().toString();
	}
}
