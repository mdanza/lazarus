package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.Corner;

import com.vividsolutions.jts.geom.Point;

@Local
public interface CornerDAO extends ModelDAO<Corner, Integer> {

	public List<Corner> findCornersWithinRadius(Point point,Double radius);
}
