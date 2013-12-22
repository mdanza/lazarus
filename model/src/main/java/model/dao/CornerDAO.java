package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.Corner;

import com.vividsolutions.jts.geom.Point;

@Local
public interface CornerDAO extends ModelDAO<Corner, Long> {

	public List<Corner> findCornersWithinRadius(Point point, Double radius);

	public List<Corner> findByStreetNames(String mainStreet, String cornerStreet);

	public Corner findClosestToPoint(Point point);

	public void removeAll();
}
