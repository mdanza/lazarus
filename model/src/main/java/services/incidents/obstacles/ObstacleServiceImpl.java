package services.incidents.obstacles;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Obstacle;
import model.User;
import model.dao.ObstacleDAO;
import model.dao.UserDAO;

import org.opengis.geometry.MismatchedDimensionException;

import services.incidents.obstacles.utils.GPScoordinateHelper;
import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.operation.distance.DistanceOp;

@Stateless(name = "ObstacleService")
public class ObstacleServiceImpl implements ObstacleService {

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
				List<Obstacle> obstacles = obstacleDAO.findAll();
				GeometryFactory factory = new GeometryFactory();
				Coordinate[] coordinates = route.toArray(new Coordinate[1]);
				CoordinateArraySequence coordinateArraySequence = new CoordinateArraySequence(
						coordinates);
				LineString lineRoute = new LineString(coordinateArraySequence,
						factory);
				if (obstacles != null) {
					for (int i = 0; i < obstacles.size(); i++) {
						Obstacle obstacle = obstacles.get(i);
						if (obstacle != null) {
							Coordinate[] points = DistanceOp.nearestPoints(
									lineRoute, obstacle.getCentre());
							if (points != null
									&& points.length == 2
									&& getDistanceInMeters(points[0], points[1]) < 30 + obstacle.getRadius()) {
								if (!toReturn.contains(obstacle)) {
									toReturn.add(obstacle);
								}
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

	private double getDistanceInMeters(Coordinate coordinate,
			Coordinate coordinate2) {
		double distance = 100000;
		if (coordinate != null && coordinate2 != null) {
			distance = GPScoordinateHelper.getDistanceBetweenPoints(
					coordinate.x, coordinate2.x, coordinate.y, coordinate2.y);
		}
		return distance;
	}
}
