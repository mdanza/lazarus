package services.directions.walking;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.Obstacle;
import model.dao.ObstacleDAO;

import org.junit.Before;
import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.incidents.obstacles.ObstacleService;
import services.shapefiles.utils.CoordinateConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vividsolutions.jts.geom.Coordinate;
public class WalkingDirectionsTest {
	
	private ObstacleService obstacleService;
	private ObstacleDAO obstacleDAO;

	CoordinateConverter coordinateConverter;
	
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

		obstacleService = (ObstacleService) context.lookup("ObstacleServiceLocal");
		obstacleDAO = (ObstacleDAO) context.lookup("ObstacleDAOLocal");
		coordinateConverter = (CoordinateConverter) context.lookup("CoordinateConverterLocal");
	}
	

	@Test
	public void test() throws MismatchedDimensionException, FactoryException, TransformException{
		//Coordinate origin = new Coordinate(-34.84903,-56.047493);
		//Coordinate end = new Coordinate(-34.895118,-56.251466);
		
		
		
		
		List<Coordinate> route = new ArrayList<Coordinate>();
		Coordinate c1 = new Coordinate(-34.905255,-56.172219);
		Coordinate c2 = new Coordinate(-34.905079,-56.169966);
		Coordinate c3 = new Coordinate(-34.90705,-56.169709);
		route.add(c1);
		route.add(c2);
		route.add(c3);
		List<Obstacle> obstacles = obstacleDAO.findAll();
		
		List<Obstacle> walkingDirections  = obstacleService.getObstaclesForRoute(route);
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		//builder.setExclusionStrategies(new ObstacleExclusionStrategy());
		builder.registerTypeAdapter(Obstacle.class, new ObstacleSerializer());
		Gson gson = builder.create();
		System.out.println(gson.toJson(walkingDirections));
		String routeString = gson.toJson(route);
		System.out.println(gson.toJson(route));
		
		JsonArray jsonRoute = new JsonParser().parse(routeString).getAsJsonArray();
		JsonObject object = jsonRoute.get(0).getAsJsonObject();
		JsonElement x = object.get("x");
		double xd = x.getAsDouble();
		
	}

}
