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

	@EJB(name = "UserDAO")
	UserDAO userDAO;

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
		// TODO
		return "";
	}

	@DELETE
	@Path("/delete")
	public String delete(@QueryParam("username") String token) {
		return "";
	}

	@POST
	@Path("/deactivate")
	public String deactivate(@QueryParam("username") String username,
			@QueryParam("token") String token) {
		// TODO
		return "";
	}
}
