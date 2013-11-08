package services.incidents.obstacles;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Obstacle;
import model.User;
import model.dao.ObstacleDAO;
import model.dao.UserDAO;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Point;

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

	public void reportObstacle(Point position, int radius, User user,
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
			Point newPosition = coordinateConverter.convertFromWGS84(position,
					"obstacle");
			Obstacle obstacle = new Obstacle(newPosition, radius, possibleUser,
					description);
			obstacleDAO.add(obstacle);
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (FactoryException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (TransformException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void deactivateObstacle(Point position) {
		if (position == null)
			throw new IllegalArgumentException("position is null");
		try {
			Point newPosition = coordinateConverter.convertFromWGS84(position,
					"obstacle");
			Obstacle possibleObstacle = obstacleDAO.find(newPosition);
			if (possibleObstacle == null)
				throw new IllegalArgumentException("obstacle does not exist");
			obstacleDAO.delete(possibleObstacle);
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (FactoryException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (TransformException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}
