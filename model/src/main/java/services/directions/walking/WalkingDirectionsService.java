package services.directions.walking;

import java.util.List;

import javax.ejb.Local;

import com.vividsolutions.jts.geom.Coordinate;

@Local
public interface WalkingDirectionsService {
	
	/**
	 * Returns walking directions from origin to end.
	 * @param origin coordinates of origin position.
	 * @param end coordinates of ending position.
	 * @return list of WalkingPosition with coordinate in WGS84. 
	 * When an obstacle is present WalkingPosition.getCoordinates() returns the center of the obstacle in WGS84.
	 */
	public List<WalkingPosition> getWalkingDirections(Coordinate origin,Coordinate end);

}
