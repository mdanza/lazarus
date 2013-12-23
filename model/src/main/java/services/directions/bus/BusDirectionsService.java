package services.directions.bus;

import java.util.List;

import javax.ejb.Local;

import com.vividsolutions.jts.geom.Point;

@Local
public interface BusDirectionsService {

	public List<BusRide> getRoutes(Point startPoint, Point endPoint,
			int maxWalkingDistanceMeters, int pageNumber);

	public List<Transshipment> getRoutesWithTransshipment(Point startPoint,
			Point endPoint, int maxWalkingDistanceMeters, int pageNumber);

}
