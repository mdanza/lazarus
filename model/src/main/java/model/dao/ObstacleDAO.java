package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.Obstacle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@Local
public interface ObstacleDAO extends ModelDAO<Obstacle, Point> {

	List<Obstacle> findByDistance(Geometry geometry, Double distance);
	

}
