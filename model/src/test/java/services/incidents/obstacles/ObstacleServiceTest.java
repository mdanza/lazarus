package services.incidents.obstacles;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.User;
import model.dao.ObstacleDAO;
import model.dao.UserDAO;

import org.junit.Before;

public class ObstacleServiceTest {

	private ObstacleService obstacleService;
	private UserDAO userDAO;
	private ObstacleDAO obstacleDAO;

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

		obstacleService = (ObstacleService) context
				.lookup("ObstacleServiceLocal");
		userDAO = (UserDAO) context.lookup("UserDAOLocal");
		obstacleDAO = (ObstacleDAO) context.lookup("ObstacleDAOLocal");
		// Create User
		User user = userDAO.find("mateo");
		if (user == null) {
			user = new User();
			user.setActive(true);
			user.setEmail("mdr@gmail.com");
			user.setPassword("111");
			user.setRole(model.User.Role.USER);
			user.setUsername("mateo");
			userDAO.add(user);
		}

	}

	// @Test
	// public void testReportObstacle() {
	// Coordinate position = new Coordinate(-34.902651,-56.162756);
	// GeometryFactory factory = new GeometryFactory();
	// Point point = factory.createPoint(position);
	//
	// if (obstacleDAO.find(point) != null) {
	// obstacleService.deactivateObstacle(point);
	// }
	//
	// User user = userDAO.find("mateo");
	// obstacleService.reportObstacle(point, 2, user, null);
	// boolean added = false;
	// Obstacle obstacle = obstacleDAO.find(point);
	// if (obstacle != null && point.equals(obstacle.getCentre())
	// && obstacle.getRadius() == 2) {
	// User possibleUser = obstacle.getUser();
	// if (possibleUser != null && possibleUser.equals(user)) {
	// added = true;
	// }
	// }
	// /*
	// boolean deleted = false;
	// obstacleService.deactivateObstacle(point);
	// if (obstacleDAO.find(point) == null) {
	// deleted = true;
	// }
	// assert (added && deleted);
	// */
	//
	// }

}
