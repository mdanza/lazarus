package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import model.Role;
import model.User;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

@Stateless(name = "UserService")
@Path("/api/UserService")
@Produces({ "text/html", "application/json" })
public class UserService {

	static Logger logger = Logger.getLogger(UserService.class);

	@EJB(name = "UserDAO")
	UserDAO userDAO;

	@GET
	@Path("/register")
	public String register(@QueryParam("username") String username,
			@QueryParam("password") String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);

		Role role = new Role();
		role.setName("probando");
		user.setRole(role);

		userDAO.add(user);
		return "User added successfuly";
	}
}
