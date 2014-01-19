package services.directions.bus;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.BusRouteMaximal;
import model.BusStop;
import model.ShapefileWKT;
import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "BusDirectionsService")
public class BusDirectionsServiceImpl implements BusDirectionsService {

	private final int pageSize = 30;

	@EJB(name = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public List<BusRide> getRoutes(Point startPoint, Point endPoint,
			int distanceMeters, int pageNumber) {
		Query q = entityManager
				.createQuery("SELECT DISTINCT startStop, endStop, route.trajectory, route.lineName, route.subLineDescription, route.subLineCode, route.destination, (distance(:startPoint, startStop.point) + distance(:endPoint, endStop.point))/2 AS averageDistance FROM BusRouteMaximal route, BusStop startStop, BusStop endStop WHERE startStop.active = 'TRUE' AND endStop.active = 'TRUE' AND startStop.variantCode = route.variantCode AND startStop.variantCode = endStop.variantCode AND startStop.ordinal < endStop.ordinal AND dwithin(startStop.point, :startPoint, :distance) = true AND dwithin(endStop.point, :endPoint, :distance) = true ORDER BY averageDistance ASC");
		q.setParameter("startPoint", startPoint);
		q.setParameter("endPoint", endPoint);
		q.setParameter("distance", distanceMeters);
		q.setMaxResults(pageSize);
		q.setFirstResult(pageNumber * pageSize);
		List<Object[]> queryResult = q.getResultList();
		List<BusRide> result = new ArrayList<BusRide>();
		for (Object[] o : queryResult) {
			BusStop startStop = new BusStop((BusStop) o[0]);
			BusStop endStop = new BusStop((BusStop) o[1]);
			BusStop previousStop = null;
			BusStop secondPreviousStop = null;
			if (endStop.getOrdinal() - startStop.getOrdinal() >= 1) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode", startStop.getVariantCode());
				q.setParameter("ordinal", endStop.getOrdinal() - 1);
				previousStop = new BusStop((BusStop) q.getSingleResult());
			}
			if (endStop.getOrdinal() - startStop.getOrdinal() >= 2) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode", startStop.getVariantCode());
				q.setParameter("ordinal", endStop.getOrdinal() - 2);
				secondPreviousStop = new BusStop((BusStop) q.getSingleResult());
			}
			try {

				startStop.setPoint(coordinateConverter.convertToWGS84(
						startStop.getPoint(), ShapefileWKT.BUS_STOP));

				endStop.setPoint(coordinateConverter.convertToWGS84(
						endStop.getPoint(), ShapefileWKT.BUS_STOP));

				previousStop.setPoint(coordinateConverter.convertToWGS84(
						previousStop.getPoint(), ShapefileWKT.BUS_STOP));

				secondPreviousStop.setPoint(coordinateConverter.convertToWGS84(
						secondPreviousStop.getPoint(), ShapefileWKT.BUS_STOP));
			} catch (Exception e) {
				e.printStackTrace();
			}

			result.add(new BusRide(startStop, endStop, (String) o[3],
					(String) o[4], (Long) o[5], previousStop,
					secondPreviousStop, (String) o[6]));
		}
		return result;
	}

	public List<Transshipment> getRoutesWithTransshipment(Point startPoint,
			Point endPoint, int distanceMeters, int pageNumber) {
		Query q = entityManager
				.createQuery("SELECT DISTINCT firstStop, secondStop, thirdStop, fourthStop, firstRoute, secondRoute, (distance(:startPoint, firstStop.point) + distance(secondStop.point, thirdStop.point) + distance(:endPoint, fourthStop.point))/3 AS averageDistance FROM BusStop firstStop, BusStop secondStop, BusStop thirdStop, BusStop fourthStop, BusRouteMaximal firstRoute, BusRouteMaximal secondRoute WHERE firstStop.active = true AND secondStop.active = true AND thirdStop.active = true AND fourthStop.active = true AND firstRoute.variantCode = firstStop.variantCode AND firstStop.variantCode = secondStop.variantCode AND firstStop.ordinal < secondStop.ordinal AND secondRoute.variantCode = thirdStop.variantCode AND thirdStop.variantCode = fourthStop.variantCode AND thirdStop.ordinal < fourthStop.ordinal AND dwithin(firstStop.point, :startPoint, :distance) = true AND dwithin(fourthStop.point, :endPoint, :distance) = true AND dwithin(secondStop.point, thirdStop.point, :distance) = true ORDER BY averageDistance ASC");
		q.setParameter("startPoint", startPoint);
		q.setParameter("endPoint", endPoint);
		q.setParameter("distance", distanceMeters);
		q.setMaxResults(pageSize);
		q.setFirstResult(pageNumber * pageSize);
		List<Object[]> queryResult = q.getResultList();
		List<Transshipment> result = new ArrayList<Transshipment>();
		for (Object[] o : queryResult) {
			BusStop firstRouteStartStop = new BusStop((BusStop) o[0]);
			BusStop firstRouteEndStop = new BusStop((BusStop) o[1]);
			BusStop firstRoutePreviousStop = null;
			BusStop firstRouteSecondPreviousStop = null;
			BusStop secondRouteStartStop = new BusStop((BusStop) o[2]);
			BusStop secondRouteEndStop = new BusStop((BusStop) o[3]);
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
				firstRoutePreviousStop = new BusStop(
						(BusStop) q.getSingleResult());
			}
			if (firstRouteEndStop.getOrdinal()
					- firstRouteStartStop.getOrdinal() >= 2) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode",
						firstRouteStartStop.getVariantCode());
				q.setParameter("ordinal", firstRouteEndStop.getOrdinal() - 2);
				firstRouteSecondPreviousStop = new BusStop(
						(BusStop) q.getSingleResult());
			}
			if (secondRouteEndStop.getOrdinal()
					- secondRouteStartStop.getOrdinal() >= 1) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode",
						secondRouteStartStop.getVariantCode());
				q.setParameter("ordinal", secondRouteEndStop.getOrdinal() - 1);
				secondRoutePreviousStop = new BusStop(
						(BusStop) q.getSingleResult());
			}
			if (secondRouteEndStop.getOrdinal()
					- secondRouteStartStop.getOrdinal() >= 2) {
				q = entityManager
						.createNamedQuery("BusStop.findByOrdinalFromSameLine");
				q.setParameter("variantCode",
						secondRouteStartStop.getVariantCode());
				q.setParameter("ordinal", secondRouteEndStop.getOrdinal() - 2);
				secondRouteSecondPreviousStop = new BusStop(
						(BusStop) q.getSingleResult());
			}
			try {

				firstRouteStartStop.setPoint(coordinateConverter
						.convertToWGS84(firstRouteStartStop.getPoint(),
								ShapefileWKT.BUS_STOP));

				firstRouteEndStop.setPoint(coordinateConverter.convertToWGS84(
						firstRouteEndStop.getPoint(), ShapefileWKT.BUS_STOP));

				firstRoutePreviousStop.setPoint(coordinateConverter
						.convertToWGS84(firstRoutePreviousStop.getPoint(),
								ShapefileWKT.BUS_STOP));

				firstRouteSecondPreviousStop.setPoint(coordinateConverter
						.convertToWGS84(
								firstRouteSecondPreviousStop.getPoint(),
								ShapefileWKT.BUS_STOP));

				secondRouteStartStop.setPoint(coordinateConverter
						.convertToWGS84(secondRouteStartStop.getPoint(),
								ShapefileWKT.BUS_STOP));

				secondRouteEndStop.setPoint(coordinateConverter.convertToWGS84(
						secondRouteEndStop.getPoint(), ShapefileWKT.BUS_STOP));

				secondRoutePreviousStop.setPoint(coordinateConverter
						.convertToWGS84(secondRoutePreviousStop.getPoint(),
								ShapefileWKT.BUS_STOP));

				secondRouteSecondPreviousStop.setPoint(coordinateConverter
						.convertToWGS84(
								secondRouteSecondPreviousStop.getPoint(),
								ShapefileWKT.BUS_STOP));
			} catch (Exception e) {
				e.printStackTrace();
			}
			result.add(new Transshipment(new BusRide(firstRouteStartStop,
					firstRouteEndStop, firstRoute.getLineName(), firstRoute
							.getSubLineDescription(), firstRoute
							.getSubLineCode(), firstRoutePreviousStop,
					firstRouteSecondPreviousStop, firstRoute.getDestination()),
					new BusRide(secondRouteStartStop, secondRouteEndStop,
							secondRoute.getLineName(), secondRoute
									.getSubLineDescription(), secondRoute
									.getSubLineCode(), secondRoutePreviousStop,
							secondRouteSecondPreviousStop, secondRoute
									.getDestination())));
			// result.add(new Transshipment(new BusRide(firstRouteStartStop,
			// firstRouteEndStop, firstRoute.getTrajectory(), firstRoute
			// .getLineName(), firstRoute.getSubLineDescription(),
			// firstRoute.getSubLineCode(), firstRoutePreviousStop,
			// firstRouteSecondPreviousStop), new BusRide(
			// secondRouteStartStop, secondRouteEndStop, secondRoute
			// .getTrajectory(), secondRoute.getLineName(),
			// secondRoute.getSubLineDescription(), secondRoute
			// .getSubLineCode(), secondRoutePreviousStop,
			// secondRouteSecondPreviousStop)));
		}
		return result;
	}
}
