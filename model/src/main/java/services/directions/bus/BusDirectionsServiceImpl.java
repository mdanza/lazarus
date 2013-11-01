package services.directions.bus;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.BusStop;

import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "BusDirectionsService")
public class BusDirectionsServiceImpl implements BusDirectionsService {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public List<BusRide> getRoutes(Point startPoint, Point endPoint,
			int distanceMeters) {
		Query q = entityManager
				.createQuery("SELECT startStop, endStop, route.trajectory, route.lineName, route.subLineDescription  FROM BusRouteMaximal route, BusStop startStop, BusStop endStop WHERE startStop.variantCode = route.variantCode AND startStop.variantCode = endStop.variantCode AND startStop.ordinal < endStop.ordinal AND dwithin(startStop.point, :startPoint, :distance) = true AND dwithin(endStop.point, :endPoint, :distance) = true");
		q.setParameter("startPoint", startPoint);
		q.setParameter("endPoint", endPoint);
		q.setParameter("distance", distanceMeters);
		List<Object[]> queryResult = q.getResultList();
		List<BusRide> result = new ArrayList<BusRide>();
		for (Object[] o : queryResult) {
			result.add(new BusRide((BusStop) o[0], (BusStop) o[1],
					(MultiLineString) o[2], (String) o[3], (String) o[4]));
		}
		return result;
	}

	public List<Transshipment> getRoutesWithTransshipment(Point startPoint,
			Point endPoint, int distanceMeters) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BusStop> getStopsFromPoint(Point point, int distance) {
		Query q = entityManager
				.createQuery("SELECT b FROM BusStop b WHERE dwithin(b.point, :point, :distance) = true");
		q.setParameter("point", point);
		q.setParameter("distance", distance);
		return q.getResultList();
	}
}
