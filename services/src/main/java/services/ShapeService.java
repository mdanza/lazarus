package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;
import services.shapefiles.streets.StreetLoader;

@Stateless(name = "ShapeService")
@Path("/api/shapes")
public class ShapeService {

	static Logger logger = Logger.getLogger(UserService.class);

	@EJB(name = "StreetLoader")
	private StreetLoader streetLoader;
	
	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@POST
	@Path("/uploadStreets")
	public String register(@QueryParam("url") String url) {
		if(url==null || url.equals(""))
			throw new IllegalArgumentException("Url cannot be empty or null");
		streetLoader.readShp(url);
		return "Done";
	}

}
