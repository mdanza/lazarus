package services.authentication;


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

public class AuthenticationTest {

	private UserDAO userDAO;
	private AuthenticationService authenticationService;

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
		authenticationService = (AuthenticationService) context.lookup("AuthenticationServiceLocal");

	}

	@Test
	public void testAuthenticateUser() {
		String token1 = authenticationService.authenticate("mateo", "111");
		String token2 = authenticationService.authenticate("mateoAdmin", "111");
		User user = authenticationService.authenticate(token1);
		User user2 = authenticationService.authenticate(token2);
		if(token1!=null && token2!=null && user!=null && user2!=null)
			assert(true);
	}
	
}