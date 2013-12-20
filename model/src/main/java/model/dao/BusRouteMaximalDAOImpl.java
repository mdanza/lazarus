package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.BusRouteMaximal;

@Stateless(name = "BusRouteMaximalDAO")
public class BusRouteMaximalDAOImpl implements BusRouteMaximalDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(BusRouteMaximal modelObject) {
		entityManager.persist(modelObject);
	}

	public void delete(BusRouteMaximal modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(BusRouteMaximal modelObjectOld,
			BusRouteMaximal modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public BusRouteMaximal find(Long uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
