package services.directions.bus;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.BusRouteMaximal;
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
				.createQuery("SELECT DISTINCT startStop, endStop, route.trajectory, route.lineName, route.subLineDescription, route.subLineCode, (distance(:startPoint, startStop.point) + distance(:endPoint, endStop.point))/2 AS averageDistance FROM BusRouteMaximal route, BusStop startStop, BusStop endStop WHERE startStop.variantCode = route.variantCode AND startStop.variantCode = endStop.variantCode AND startStop.ordinal < endStop.ordinal AND dwithin(startStop.point, :startPoint, :distance) = true AND dwithin(endStop.point, :endPoint, :distance) = true ORDER BY averageDistance ASC");
		q.setParameter("startPoint", startPoint);
		q.setParameter("endPoint", endPoint);
		q.setParameter("distance", distanceMeters);
		q.setMaxResults(10);
		List<Object[]> queryResult = q.getResultList();
		List<BusRide> result = new ArrayList<BusRide>();
		for (Object[] o : queryResult) {
			BusStop startStop = (BusStop) o[0];
			BusStop endStop = (BusStop) o[1];
			BusStop previousStop = null;
			BusStop secondPreviousStop = null;
			if (endStop.getOrdinal() - startStop.getOrdinal() >= 1) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode", startStop.getVariantCode());
				q.setParameter("ordinal", endStop.getOrdinal() - 1);
				previousStop = (BusStop) q.getSingleResult();
			}
			if (endStop.getOrdinal() - startStop.getOrdinal() >= 2) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode", startStop.getVariantCode());
				q.setParameter("ordinal", endStop.getOrdinal() - 2);
				secondPreviousStop = (BusStop) q.getSingleResult();
			}
			result.add(new BusRide(startStop, endStop, (MultiLineString) o[2],
					(String) o[3], (String) o[4], (Integer) o[5], previousStop,
					secondPreviousStop));
		}
		return result;
	}

	public List<Transshipment> getRoutesWithTransshipment(Point startPoint,
			Point endPoint, int distanceMeters) {
		// Query q =
		// entityManager.createQuery("SELECT DISTINCT firstStop, secondStop, thirdStop, fourthStop, firstRoute, secondRoute, (distance(:startPoint, firstStop.point) + distance(:endPoint, fourthStop.point))/2 AS averageDistance FROM BusStop firstStop, BusStop secondStop, BusStop thirdStop, BusStop fourthStop, BusRouteMaximal firstRoute, BusRouteMaximal secondRoute WHERE firstRoute.variantCode = firstStop.variantCode AND firstStop.variantCode = secondStop.variantCode AND firstStop.ordinal < secondStop.ordinal AND secondRoute.variantCode = thirdStop.variantCode AND thirdStop.variantCode = fourthStop.variantCode AND thirdStop.ordinal < fourthStop.ordinal AND dwithin(firstStop.point, :startPoint, :distance) = true AND dwithin(fourthStop.point, :endPoint, :distance) = true AND secondStop.busStopCode = thirdStop.busStopCode ORDER BY averageDistance ASC");
		Query q = entityManager
				.createQuery("SELECT DISTINCT firstStop, secondStop, thirdStop, fourthStop, firstRoute, secondRoute, (distance(:startPoint, firstStop.point) + distance(secondStop.point, thirdStop.point) + distance(:endPoint, fourthStop.point))/3 AS averageDistance FROM BusStop firstStop, BusStop secondStop, BusStop thirdStop, BusStop fourthStop, BusRouteMaximal firstRoute, BusRouteMaximal secondRoute WHERE firstRoute.variantCode = firstStop.variantCode AND firstStop.variantCode = secondStop.variantCode AND firstStop.ordinal < secondStop.ordinal AND secondRoute.variantCode = thirdStop.variantCode AND thirdStop.variantCode = fourthStop.variantCode AND thirdStop.ordinal < fourthStop.ordinal AND dwithin(firstStop.point, :startPoint, :distance) = true AND dwithin(fourthStop.point, :endPoint, :distance) = true AND dwithin(secondStop.point, thirdStop.point, :distance) = true ORDER BY averageDistance ASC");
		q.setParameter("startPoint", startPoint);
		q.setParameter("endPoint", endPoint);
		q.setParameter("distance", distanceMeters);
		q.setMaxResults(10);
		List<Object[]> queryResult = q.getResultList();
		List<Transshipment> result = new ArrayList<Transshipment>();
		for (Object[] o : queryResult) {
			BusStop firstRouteStartStop = (BusStop) o[0];
			BusStop firstRouteEndStop = (BusStop) o[1];
			BusStop firstRoutePreviousStop = null;
			BusStop firstRouteSecondPreviousStop = null;
			BusStop secondRouteStartStop = (BusStop) o[2];
			BusStop secondRouteEndStop = (BusStop) o[3];
			BusStop secondRoutePreviousStop = null;
			BusStop secondRouteSecondPreviousStop = null;
			BusRouteMaximal firstRoute = (BusRouteMaximal) o[4];
			BusRouteMaximal secondRoute = (BusRouteMaximal) o[5];

			if (firstRouteEndStop.getOrdinal()
					- firstRouteStartStop.getOrdinal() >= 1) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode",
						firstRouteStartStop.getVariantCode());
				q.setParameter("ordinal", firstRouteEndStop.getOrdinal() - 1);
				firstRoutePreviousStop = (BusStop) q.getSingleResult();
			}
			if (firstRouteEndStop.getOrdinal()
					- firstRouteStartStop.getOrdinal() >= 2) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode",
						firstRouteStartStop.getVariantCode());
				q.setParameter("ordinal", firstRouteEndStop.getOrdinal() - 2);
				firstRouteSecondPreviousStop = (BusStop) q.getSingleResult();
			}
			if (secondRouteEndStop.getOrdinal()
					- secondRouteStartStop.getOrdinal() >= 1) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode",
						secondRouteStartStop.getVariantCode());
				q.setParameter("ordinal", secondRouteEndStop.getOrdinal() - 1);
				secondRoutePreviousStop = (BusStop) q.getSingleResult();
			}
			if (secondRouteEndStop.getOrdinal()
					- secondRouteStartStop.getOrdinal() >= 2) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode",
						secondRouteStartStop.getVariantCode());
				q.setParameter("ordinal", secondRouteEndStop.getOrdinal() - 2);
				secondRouteSecondPreviousStop = (BusStop) q.getSingleResult();
			}
			result.add(new Transshipment(new BusRide(firstRouteStartStop,
					firstRouteEndStop, firstRoute.getTrajectory(), firstRoute
							.getLineName(), firstRoute.getSubLineDescription(),
					firstRoute.getSubLineCode(), firstRoutePreviousStop,
					firstRouteSecondPreviousStop), new BusRide(
					secondRouteStartStop, secondRouteEndStop, secondRoute
							.getTrajectory(), secondRoute.getLineName(),
					secondRoute.getSubLineDescription(), secondRoute
							.getSubLineCode(), secondRoutePreviousStop,
					secondRouteSecondPreviousStop)));
		}
		return result;
	}
}
