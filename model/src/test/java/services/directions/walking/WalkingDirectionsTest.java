package services.directions.walking;


import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import services.directions.walking.WalkingDirectionsService;

import org.junit.Before;
public class WalkingDirectionsTest {
	
	private WalkingDirectionsService walkingDirectionsService;

	@Before
	public void configure() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.core.LocalInitialContextFactory");

		p.put("openejb.deployments.classpath.ear", "true");

		p.put("lazarus-persistence-unit", "new://Resource?type=DataSource");
		p.put("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
		p.put("lazarus-persistence-unit.JdbcUrl", "jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.JdbcUrl", "jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.Username", "postgres");
		p.put("lazarus-persistence-unit.Password", "postgres");

		Context context = new InitialContext(p);

		walkingDirectionsService = (WalkingDirectionsService) context.lookup("WalkingDirectionsServiceLocal");
	}

//	@Test
//	public void test(){
//		//Coordinate origin = new Coordinate(-34.84903,-56.047493);
//		//Coordinate end = new Coordinate(-34.895118,-56.251466);
//		Coordinate origin = new Coordinate(-34.911062,-56.154312);
//		Coordinate end = new Coordinate(-34.905713,-56.202303);
//		List<WalkingPosition> walkingDirections  = walkingDirectionsService.getWalkingDirections(origin, end);
//		StringBuilder buildReturn = new StringBuilder();
//		for (WalkingPosition walkingPosition : walkingDirections) {
//			Coordinate coordinate = walkingPosition.getCoordinate();
//			String instruction = walkingPosition.getInstruction();
//			Obstacle obstacle = walkingPosition.getObstacle();
//			buildReturn.append(coordinate.x+","+coordinate.y+",");
//			if (instruction != null) {
//				buildReturn.append(instruction);
//			} else if (obstacle != null) {
//				buildReturn.append("obstacle,radius:"+obstacle.getRadius()+",description:"+obstacle.getDescription());
//			}
//			buildReturn.append(System.getProperty("line.separator"));
//		}
//		System.out.println(buildReturn.toString());
//	}

}
