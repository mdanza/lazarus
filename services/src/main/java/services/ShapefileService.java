package services;

import helpers.RestResultsHelper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import model.User;
import model.User.Role;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;
import services.shapefiles.ShapefileLoader;
import services.shapefiles.ShapefileStatusService;
import services.shapefiles.ShapefileStatusService.ShapefileStatus;
import services.shapefiles.address.AddressLoader;
import services.shapefiles.bus.BusRoutesMaximalLoader;
import services.shapefiles.bus.BusStopLoader;
import services.shapefiles.bus.ControlPointLoader;
import services.shapefiles.corner.CornerLoader;
import services.shapefiles.streets.StreetLoader;

import com.google.gson.Gson;

@Stateless(name = "ShapeService")
@Path("v1/api/shapes")
public class ShapefileService {

	static Logger logger = Logger.getLogger(ShapefileService.class);

	private Gson gson = new Gson();

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	@EJB(name = "ShapefileStatusService")
	private ShapefileStatusService shapefileStatusService;

	@EJB(name = "StreetLoader")
	private StreetLoader streetLoader;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "BusStopLoader")
	private BusStopLoader busStopLoader;

	@EJB(name = "BusRoutesMaximalLoader")
	private BusRoutesMaximalLoader busRoutesMaximalLoader;

	@EJB(name = "ControlPointLoader")
	private ControlPointLoader controlPointLoader;

	@EJB(name = "AddressLoader")
	private AddressLoader addressLoader;

	@EJB(name = "CornerLoader")
	private CornerLoader cornerLoader;

	@POST
	@Path("/uploadStreets")
	public String uploadStreets(@HeaderParam("Authorization") String token,
			@FormParam("url") String url) {
		if (url == null || url.equals("") || token == null || token.equals(""))
			throw new IllegalArgumentException(
					"Url and token cannot be empty or null");
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			streetLoader.updateShp(url);
			return restResultsHelper.resultWrapper(true, gson.toJson("Successfully received shapefile"));
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@POST
	@Path("/uploadAddresses")
	public String uploadAddress(@HeaderParam("Authorization") String token,
			@FormParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		addressLoader.updateShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadBusStops")
	public String busStops(@HeaderParam("Authorization") String token,
			@FormParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		busStopLoader.updateShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadControlPoints")
	public String controlPoints(@HeaderParam("Authorization") String token,
			@FormParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		controlPointLoader.updateShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadBusRoutesMaximal")
	public String busRoutesMaximal(@HeaderParam("Authorization") String token,
			@FormParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		busRoutesMaximalLoader.updateShp(url);
		return "Done";
	}

	@POST
	@Path("/uploadCorners")
	public String uploadCorners(@HeaderParam("Authorization") String token,
			@FormParam("url") String url) {
		if (url == null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		cornerLoader.updateShp(url);
		return "Done";
	}

	@GET
	@Path("/status")
	public String getUploadStatus(@HeaderParam("Authorization") String token) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token cannot be empty or null");
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			ShapefileStatus result = shapefileStatusService.getUploadStatus();
			return restResultsHelper.resultWrapper(true, gson.toJson(result));
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}
	
	private class ShapefileUploaderTask implements Runnable{
		private ShapefileLoader loader;

		public ShapefileUploaderTask(ShapefileLoader loader) {
			this.loader = loader;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
		
	}

}
