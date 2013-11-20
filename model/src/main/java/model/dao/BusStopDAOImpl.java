package model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.BusStop;

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

	public BusStop find(Integer uniqueKey) {
		Query q = entityManager.createNamedQuery("BusStop.findById");
		q.setParameter("id", uniqueKey);
		BusStop result = (BusStop) q.getSingleResult();
		return result;
	}

	public List<BusStop> getLineStops(int variantCode) {
		Query q = entityManager.createNamedQuery("BusStop.findByVariantCode");
		q.setParameter("variantCode", variantCode);
		List<BusStop> result = q.getResultList();
		return result;
	}
}
