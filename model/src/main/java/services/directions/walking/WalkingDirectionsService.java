package services.directions.walking;

import java.util.Map;

import javax.ejb.Local;

import com.vividsolutions.jts.geom.Coordinate;

@Local
public interface WalkingDirectionsService {
	
	public Map<Coordinate,String> getWalkingDirections(Coordinate origin,Coordinate end);

}
