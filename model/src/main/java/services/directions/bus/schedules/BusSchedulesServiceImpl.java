package services.directions.bus.schedules;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Bus;

@Stateless(name = "BusSchedulesService")
public class BusSchedulesServiceImpl implements BusSchedulesService {
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public Bus getClosestBus(int variantCode, int subLineCode,
			int maximumBusStopOrdinal) {
		Query q = entityManager
				.createQuery("SELECT Bus b FROM Bus b WHERE b.variantCode = :variantCode AND b.subLineCode = :subLineCode AND b.lastPassedStopOrdinal < :maximumOrdinal ORDER BY b.lastPassedStopOrdinal DESC");
		q.setParameter("variantCode", variantCode);
		q.setParameter("subLineCode", subLineCode);
		q.setParameter("maximumOrdinal", maximumBusStopOrdinal);
		q.setMaxResults(1);
		try {
			return (Bus) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public int[] getBusLineSchedule(int variantCode, int subLineCode,
			int busStopLocationCode) {
		// TODO Auto-generated method stub
		return null;
	}

}
