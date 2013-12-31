package services.users;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.Favourite;
import model.User;
import model.dao.FavouriteDAO;
import model.dao.UserDAO;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class CreateUserTest {

	private FavouriteService favouriteService;
	private UserDAO userDAO;
	private FavouriteDAO favouriteDAO;

	@Before
	public void configure() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.core.LocalInitialContextFactory");

		p.put("openejb.deployments.classpath.ear", "true");

		p.put("lazarus-persistence-unit", "new://Resource?type=DataSource");
		p.put("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
		p.put("lazarus-persistence-unit.JdbcUrl",
				"jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.JdbcUrl",
				"jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.Username", "postgres");
		p.put("lazarus-persistence-unit.Password", "postgres");

		Context context = new InitialContext(p);

		userDAO = (UserDAO) context.lookup("UserDAOLocal");

	}

	@Test
	public void testCreateUser() {
		User user = new User();
		user.setUsername("mateo");
		user.setPassword("111");
		user.setEmail("mdanza@gmail.com");
		user.setActive(true);
		user.setRole(model.User.Role.USER);
		try {
			userDAO.add(user);
		} catch (Exception e) {
			assert(false);
		}
	}
	
	@Test
	public void testCreateAdminUser() {
		User user = new User();
		user.setUsername("mateoAdmin");
		user.setPassword("111");
		user.setEmail("mdanzaAdmin@gmail.com");
		user.setActive(true);
		user.setRole(model.User.Role.ADMIN);
		try {
			userDAO.add(user);
		} catch (Exception e) {
			assert(false);
		}
	}
}
