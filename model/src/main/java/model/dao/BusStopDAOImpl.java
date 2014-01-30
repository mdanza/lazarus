package model.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.BusStop;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "BusStopDAO")
public class BusStopDAOImpl implements BusStopDAO {
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(BusStop modelObject) {
		entityManager.persist(modelObject);
	}

	public void delete(BusStop modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(BusStop modelObjectOld, BusStop modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public BusStop find(Long uniqueKey) {
		Query q = entityManager.createNamedQuery("BusStop.findById");
		q.setParameter("id", uniqueKey);
		BusStop result = (BusStop) q.getSingleResult();
		return result;
	}

	public List<BusStop> getLineStops(long variantCode) {
		Query q = entityManager.createNamedQuery("BusStop.findByVariantCode");
		q.setParameter("variantCode", variantCode);
		List<BusStop> result = q.getResultList();
		return result;
	}

	@Override
	public void removeAll() {
		entityManager.createNamedQuery("BusStop.removeAll").executeUpdate();
	}

	@Override
	public List<BusStop> getDistinctLocationCodeBusStops(int page) {
		final int pageSize = 1000;
		List<BusStop> result = null;
		Query q = entityManager
				.createQuery(
						"SELECT DISTINCT busStopLocationCode, point, active FROM BusStop b");
		q.setMaxResults(pageSize);
		q.setFirstResult(page * pageSize);
		List<Object[]> queryResult = q
				.getResultList();
		if (queryResult != null && queryResult.size() != 0) {
			result = new ArrayList<BusStop>();
			for (Object[] row : queryResult)
				result.add(new BusStop((Long) row[0], (Point) row[1],
						(Boolean) row[2]));
		}
		return result;
	}

	@Override
	public void changeActiveFieldByLocationCode(long locationCode,
			boolean active) {
		Query q = entityManager.createNamedQuery("BusStop.updateActiveField");
		q.setParameter("busStopLocationCode", locationCode);
		q.setParameter("active", active);
		q.executeUpdate();
	}
}
