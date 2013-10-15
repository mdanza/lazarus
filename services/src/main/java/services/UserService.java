package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.Role;
import model.User;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;

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
		if (username == null || username == "" || password == null
				|| password == "")
			throw new IllegalArgumentException(
					"No nulls nor empty strings allowd");
		return authenticationService.authenticate(username, password);
	}

	@DELETE
	@Path("/delete")
	public String delete(@QueryParam("token") String token,
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
			@QueryParam("token") String token) {
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
}
