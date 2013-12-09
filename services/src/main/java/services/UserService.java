package services;

import helpers.RestResultsHelper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import model.Role;
import model.User;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;

import com.google.gson.Gson;

@Stateless(name = "UserService")
@Path("v1/api/users")
public class UserService {

	static Logger logger = Logger.getLogger(UserService.class);

	private static final String USER_ROLE_NAME = "user";
	private static final String ADMIN_ROLE_NAME = "admin";

	@EJB(name = "UserDAO")
	private UserDAO userDAO;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	private Gson gson = new Gson();

	@POST
	public String register(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("email") String email,
			@FormParam("cellphone") String cellphone,
			@FormParam("secretQuestion") String secretQuestion,
			@FormParam("secretAnswer") String secretAnswer) {
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
		try {
			userDAO.add(user);
			return restResultsHelper.resultWrapper(true,
					"User added successfuly");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "could not add user");
		}
	}

	@POST
	@Path("/login")
	public String login(@FormParam("username") String username,
			@FormParam("password") String password) {
		if (username == null || username.equals("") || password == null
				|| password.equals(""))
			throw new IllegalArgumentException(
					"No nulls nor empty strings allowed");
		try {
			return restResultsHelper.resultWrapper(true,
					authenticationService.authenticate(username, password));
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "login error");
		}
	}

	@DELETE
	public String delete(@HeaderParam("Authorization") String token,
			@FormParam("username") String username) {
		User actionUser = authenticationService.authenticate(token);
		User deleting = userDAO.find(username);
		if (actionUser.getUsername().equals(username)
				|| actionUser.getRole().getName().equals(ADMIN_ROLE_NAME)) {
			userDAO.delete(deleting);
			return "deleted";
		}
		throw new IllegalArgumentException("Invalid credentials");
	}

	@PUT
	public String modify(@FormParam("username") String username,
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

	@GET
	public String getUser(@HeaderParam("Authorization") String token) {
		try {
			User user = authenticationService.authenticate(token);
			return restResultsHelper.resultWrapper(true, gson.toJson(user));
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "could not get user");
		}
	}
}
