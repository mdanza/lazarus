package services;

import helpers.RestResultsHelper;

import java.lang.reflect.Type;
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

import model.Favourite;
import model.User;
import services.authentication.AuthenticationService;
import services.users.FavouriteExclusionStrategy;
import services.users.FavouriteService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "FavouritesReportingService")
@Path("v1/api/favourites")
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

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	@POST
	public String addToFavourite(@HeaderParam("Authorization") String token,
			@FormParam("coordinates") String coordinates,
			@FormParam("name") String name) {
		if (token == null || token.equals("") || coordinates == null
				|| coordinates.equals("") || name == null || name.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token, coordinates or name empty or null");
		try {
			User user = authenticationService.authenticate(token);
			Double x = Double.valueOf(coordinates.split(",")[0]);
			Double y = Double.valueOf(coordinates.split(",")[1]);
			Coordinate position = new Coordinate(x, y);
			GeometryFactory factory = new GeometryFactory();
			Point point = factory.createPoint(position);
			try {
				favouriteService.addToFavourite(user, point, name);
				return restResultsHelper.resultWrapper(true,
						"Successfully added favourite");
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"Could not add favourite");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@DELETE
	@Path("/{name}")
	public String removeFavourite(@HeaderParam("Authorization") String token,
			@PathParam("name") String name) {
		if (token == null || token.equals("") || name == null
				|| name.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token, coordinates or name empty or null");
		try {
			User user = authenticationService.authenticate(token);
			try {
				favouriteService.removeFromFavourite(user, name);
				return restResultsHelper.resultWrapper(true,
						"Successfully removed favourite");
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"Could not remove favourite");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	public String getFavourites(@HeaderParam("Authorization") String token) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token, coordinates or name empty or null");
		try {
			User user = authenticationService.authenticate(token);
			try {
				List<Favourite> favourites = favouriteService
						.getFavourites(user);
				if (favourites != null) {
					Type type = new TypeToken<List<Favourite>>() {
					}.getType();
					String serializedFavourites = gson.toJson(favourites, type);
					return restResultsHelper.resultWrapper(true,
							serializedFavourites);
				} else
					return restResultsHelper.resultWrapper(false,
							"No favourites for given user");
			} catch (Exception e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						"Could not get favourites");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

}
