package services.directions.walking;

import java.util.List;

import javax.ejb.Local;

import com.vividsolutions.jts.geom.Coordinate;

@Local
public interface WalkingDirectionsService {
	
	/*
	 * Returns walking directions from origin to end
	 * @param origin coordinates of origin
	 * @param 
	 * @return null if no path was found, coordinate with 
	 */
	public List<Object[]> getWalkingDirections(Coordinate origin,Coordinate end);

}
