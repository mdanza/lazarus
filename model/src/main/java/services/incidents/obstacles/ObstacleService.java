package services.incidents.obstacles;

import java.util.List;

import javax.ejb.Local;

import model.Obstacle;
import model.User;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

@Local
public interface ObstacleService {

	public void deactivateObstacle(Point position);

	public long reportObstacle(Point position, long radius, User user,
			String description);

	public List<Obstacle> getAll();

	public void deleteObstacle(long id);
	
	public List<Obstacle> getObstaclesForRoute(List<Coordinate> route);

}
