package model.dao;

import javax.ejb.Local;

import model.Obstacle;

import com.vividsolutions.jts.geom.Point;

@Local
public interface ObstacleDAO extends ModelDAO<Obstacle, Point> {
	

}
