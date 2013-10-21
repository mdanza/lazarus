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
import services.shapefiles.address.AddressLoader;
import services.shapefiles.bus.BusRoutesMaximalLoader;
import services.shapefiles.bus.BusRoutesNonMaximalLoader;
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

	@EJB(name = "BusRoutesNonMaximalLoader")
	private BusRoutesNonMaximalLoader busRoutesNonMaximalLoader;

	@EJB(name = "BusRoutesMaximalLoader")
	private BusRoutesMaximalLoader busRoutesMaximalLoader;

	@EJB(name = "BusStopDAO")
	private BusStopDAO busStopDAO;
	@EJB(name = "AddressLoader")
	private AddressLoader addressLoader;

	@POST
	@Path("/uploadStreets")
	public String uploadStreets(@QueryParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		streetLoader.readShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadAddresses")
	public String uploadAddress(@QueryParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		addressLoader.readShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadBusStops")
	public String busStops(@QueryParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		busStopLoader.readShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadBusRoutesNonMaximal")
	public String busRoutesNonMaximal(@QueryParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		busRoutesNonMaximalLoader.readShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadBusRoutesMaximal")
	public String busRoutesMaximal(@QueryParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		busRoutesMaximalLoader.readShp(url);
		return "Done";
	}

	@GET
	@Path("/getBusStop")
	public String getBusStop(@QueryParam("id") int id) {
		BusStop busStop = busStopDAO.find(id);
		return busStop.getPoint().toString();
	}
}
