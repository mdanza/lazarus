package services.incidents.obstacles;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Obstacle;
import model.User;
import model.dao.ObstacleDAO;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geomgraph.Position;

@Stateless(name = "ObstacleService")
public class ObstacleServiceImpl implements ObstacleService {

	private static Logger logger = Logger.getLogger(ObstacleServiceImpl.class);
	private int duration = 6000;

	@EJB(beanName = "UserDAO")
	protected UserDAO userDAO;

	@EJB(beanName = "ObstacleDAO")
	private ObstacleDAO obstacleDAO;

	@Override
	public void reportObstacle(Point position, int radius, User user) {
		if(position==null || user==null)
			throw new IllegalArgumentException("Position or user cannot be null");
		User possibleUser = userDAO.find(user.getUsername());
		if(possibleUser==null)
			throw new IllegalArgumentException("User does not exists");
		if(obstacleDAO.find(position)!=null)
			throw new IllegalArgumentException("Obstacle already exists in that position");
		Obstacle obstacle = new Obstacle(position,radius,possibleUser);
		obstacleDAO.add(obstacle);
	}

	@Override
	public void deactivateObstacle(Point position) {
		if(position==null)
			throw new IllegalArgumentException("position is null");
		Obstacle possibleObstacle = obstacleDAO.find(position);
		if(possibleObstacle==null)
			throw new IllegalArgumentException("obstacle does not exist");
		obstacleDAO.delete(possibleObstacle);
	}

}
