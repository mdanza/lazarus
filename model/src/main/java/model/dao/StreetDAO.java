package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.Street;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@Local
public interface StreetDAO extends ModelDAO<Street, String> {

	public List<Street> findByNameCode(String nameCode);

	public List<Street> findByName(String nameCode);

	public Street findClosestToPoint(Point point);

	public List<String> findPossibleStreets(String approximate);

	public void removeAll();
	
	public List<Object[]> getStreetsMultiLinesEnd();

}
