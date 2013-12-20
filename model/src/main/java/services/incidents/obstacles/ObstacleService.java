package services.incidents.obstacles;

import javax.ejb.Local;

import model.User;

import com.vividsolutions.jts.geom.Point;

@Local
public interface ObstacleService {
	
	public void deactivateObstacle(Point position);

	void reportObstacle(Point position, long radius, User user,
			String description);

}
