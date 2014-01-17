package services.incidents.obstacles;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Obstacle;
import model.ShapefileWKT;
import model.User;
import model.dao.ObstacleDAO;
import model.dao.UserDAO;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

@Stateless(name = "ObstacleService")
public class ObstacleServiceImpl implements ObstacleService {

	private static Logger logger = Logger.getLogger(ObstacleServiceImpl.class);
	private int duration = 6000;

	@EJB(beanName = "UserDAO")
	protected UserDAO userDAO;

	@EJB(beanName = "ObstacleDAO")
	private ObstacleDAO obstacleDAO;

	@EJB(beanName = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	public long reportObstacle(Point position, long radius, User user,
			String description) {
		if (position == null || user == null)
			throw new IllegalArgumentException(
					"Position or user cannot be null");
		User possibleUser = userDAO.find(user.getUsername());
		if (possibleUser == null)
			throw new IllegalArgumentException("User does not exists");
		if (obstacleDAO.find(position) != null)
			throw new IllegalArgumentException(
					"Obstacle already exists in that position");
		try {
			Obstacle obstacle = new Obstacle(position, radius, possibleUser,
					description);
			return obstacleDAO.addObstacle(obstacle);
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void deactivateObstacle(Point position) {
		if (position == null)
			throw new IllegalArgumentException("position is null");
		try {
			Obstacle possibleObstacle = obstacleDAO.find(position);
			if (possibleObstacle == null)
				throw new IllegalArgumentException("obstacle does not exist");
			obstacleDAO.delete(possibleObstacle);
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} 
	}

	@Override
	public void deleteObstacle(long id) {
		Obstacle o = obstacleDAO.findById(id);
		if (o == null)
			throw new IllegalArgumentException(
					"Id does not correspond to any obstacle");
		obstacleDAO.remove(o);
	}

	@Override
	public List<Obstacle> getAll() {
		return obstacleDAO.findAll();
	}

	public List<Obstacle> getObstaclesForRoute(List<Coordinate> route) {
		try {
			if (route != null && !route.isEmpty()) {
				List<Obstacle> toReturn = new ArrayList<Obstacle>();
				for (int i = 0; i < route.size() - 1; i++) {

					// Create LineString to find near obstacles
					Coordinate firstCoordinate = route.get(i);
					Coordinate secondCoordinate = route.get(i + 1);

					List<Coordinate> coordinatesList = new ArrayList<Coordinate>();
					coordinatesList.add(firstCoordinate);
					coordinatesList.add(secondCoordinate);

					Coordinate[] coordinates = coordinatesList
							.toArray(new Coordinate[0]);
					CoordinateArraySequence coordinateArraySequence = new CoordinateArraySequence(
							coordinates);

					GeometryFactory factory = new GeometryFactory();
					LineString lineRoute = new LineString(
							coordinateArraySequence, factory);

					// Find near obstacles
					List<Obstacle> nearObstacles = obstacleDAO.findByDistance(
							lineRoute, 30.0);

					if (nearObstacles != null && !nearObstacles.isEmpty()) {
						for (Obstacle obstacle : nearObstacles) {
							if (!toReturn.contains(obstacle)) {
								toReturn.add(obstacle);
							}
						}
					}

				}
				return toReturn;

			} else {
				return null;
			}
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} 
	}
}
