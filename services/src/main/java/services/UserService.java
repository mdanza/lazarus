package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.Role;
import model.User;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;
import services.incidents.obstacles.ObstacleService;
import services.users.FavouriteService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "UserService")
@Path("/api/users")
public class UserService {

	static Logger logger = Logger.getLogger(UserService.class);

	private static final String USER_ROLE_NAME = "user";
	private static final String ADMIN_ROLE_NAME = "admin";

	@EJB(name = "UserDAO")
	private UserDAO userDAO;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "ObstacleService")
	private ObstacleService obstacleService;
	
	@EJB(name = "FavouriteService")
	private FavouriteService favouriteService;

	@POST
	@Path("/create")
	public String register(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("email") String email,
			@QueryParam("cellphone") String cellphone,
			@QueryParam("secretQuestion") String secretQuestion,
			@QueryParam("secretAnswer") String secretAnswer) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.setCellphone(cellphone);
		user.setSecretQuestion(secretQuestion);
		user.setSecretAnswer(secretAnswer);
		user.setActive(true);
		Role role = new Role();
		role.setName(USER_ROLE_NAME);
		user.setRole(role);
		userDAO.add(user);
		return "User added successfuly";
	}

	@POST
	@Path("/login")
	public String login(@QueryParam("username") String username,
			@QueryParam("password") String password) {
		if (username == null || username.equals("") || password == null
				|| password.equals(""))
			throw new IllegalArgumentException(
					"No nulls nor empty strings allowd");
		return authenticationService.authenticate(username, password);
	}

	@DELETE
	@Path("/delete")
	public String delete(@HeaderParam("Authorization") String token,
			@QueryParam("username") String username) {
		User actionUser = authenticationService.authenticate(token);
		User deleting = userDAO.find(username);
		if (actionUser.getUsername().equals(username)
				|| actionUser.getRole().getName().equals(ADMIN_ROLE_NAME)) {
			userDAO.delete(deleting);
			return "deleted";
		}
		throw new IllegalArgumentException("Invalid credentials");
	}

	@POST
	@Path("/deactivate")
	public String deactivate(@QueryParam("username") String username,
			@HeaderParam("Authorization") String token) {
		User actionUser = authenticationService.authenticate(token);
		User deactivating = userDAO.find(username);
		if (actionUser.getUsername().equals(username)
				|| actionUser.getRole().getName().equals(ADMIN_ROLE_NAME)) {
			deactivating.setActive(false);
			userDAO.modify(userDAO.find(username), deactivating);
			return "deactivated";
		}
		throw new IllegalArgumentException("Invalid credentials");
	}

	@POST
	@Path("/reportObstacle")
	public String reportObstacle(@HeaderParam("Authorization") String token,
			@QueryParam("coordinates") String coordinates,
			@QueryParam("radius") String radius,
			@QueryParam("description") String description) {
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

	@POST
	@Path("/deactivateObstacle")
	public String deactivateObstacle(@HeaderParam("Authorization") String token,
			@QueryParam("coordinates") String coordinates) {
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
	

	@POST
	@Path("/addToFavourite")
	public String addToFavourite(@HeaderParam("Authorization") String token,
			@QueryParam("coordinates") String coordinates,@QueryParam("name") String name) {
		if (token == null || token.equals("") || coordinates == null
				|| coordinates.equals("") || name==null || name.equals(""))
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
	
	@POST
	@Path("/removeFavourite")
	public String removeFavourite(@HeaderParam("Authorization") String token,
			@QueryParam("name") String name) {
		if (token == null || token.equals("") || name==null || name.equals(""))
			throw new IllegalArgumentException(
					"Token, coordinates or name empty or null");
		User user = authenticationService.authenticate(token);
		favouriteService.removeFromFavourite(user, name);
		return "Done";
	}
}
