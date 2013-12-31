package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.Obstacle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@Local
public interface ObstacleDAO extends ModelDAO<Obstacle, Point> {

	public List<Obstacle> findByDistance(Geometry geometry, Double distance);
	
	public List<Obstacle> findAll();
	
	public void remove(Obstacle o);
	
	public Obstacle findById(long id);
	
	public long addObstacle(Obstacle o);
}
