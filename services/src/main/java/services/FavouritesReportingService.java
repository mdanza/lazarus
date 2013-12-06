package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import model.User;
import services.authentication.AuthenticationService;
import services.users.FavouriteExclusionStrategy;
import services.users.FavouriteService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "FavouritesReportingService")
@Path("/api/favourites")
public class FavouritesReportingService {
	
	private Gson gson = createGson();

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new FavouriteExclusionStrategy());
		return builder.create();
	}
	
	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "FavouriteService")
	private FavouriteService favouriteService;
	
	@POST
	public String addToFavourite(@HeaderParam("Authorization") String token,
			@FormParam("coordinates") String coordinates,
			@FormParam("name") String name) {
		if (token == null || token.equals("") || coordinates == null
				|| coordinates.equals("") || name == null || name.equals(""))
			throw new IllegalArgumentException(
					"Token, coordinates or name empty or null");
		User user = authenticationService.authenticate(token);
		Double x = Double.valueOf(coordinates.split(",")[0]);
		Double y = Double.valueOf(coordinates.split(",")[1]);
		Coordinate position = new Coordinate(x, y);
		GeometryFactory factory = new GeometryFactory();
		Point point = factory.createPoint(position);
		favouriteService.addToFavourite(user, point, name);
		return "Done";
	}

	@DELETE
	public String removeFavourite(@HeaderParam("Authorization") String token,
			@FormParam("name") String name) {
		if (token == null || token.equals("") || name == null
				|| name.equals(""))
			throw new IllegalArgumentException(
					"Token, coordinates or name empty or null");
		User user = authenticationService.authenticate(token);
		favouriteService.removeFromFavourite(user, name);
		return "Done";
	}

	@GET
	public String getFavourites(@HeaderParam("Authorization") String token) {
		if (token == null || token.equals(""))
			throw new IllegalArgumentException(
					"Token, coordinates or name empty or null");
		User user = authenticationService.authenticate(token);
		return gson.toJson(favouriteService.getFavourites(user));
	}

}
