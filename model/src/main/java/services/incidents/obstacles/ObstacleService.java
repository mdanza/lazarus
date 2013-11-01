package services.incidents.obstacles;

import javax.ejb.Local;

import model.User;

import com.vividsolutions.jts.geom.Point;

@Local
public interface ObstacleService {
	
	public void reportObstacle(Point position, int radius, User user);
	
	public void deactivateObstacle(Point position);

}
