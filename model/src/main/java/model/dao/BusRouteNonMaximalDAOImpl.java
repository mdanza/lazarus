package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.BusRouteNonMaximal;

@Stateless(name = "BusRouteNonMaximalDAO")
public class BusRouteNonMaximalDAOImpl implements BusRouteNonMaximalDAO{

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;
	
	public void add(BusRouteNonMaximal modelObject) {
		entityManager.persist(modelObject);
	}

	public void delete(BusRouteNonMaximal modelObject) {
		// TODO Auto-generated method stub
		
	}

	public void modify(BusRouteNonMaximal modelObjectOld,
			BusRouteNonMaximal modelObjectNew) {
		// TODO Auto-generated method stub
		
	}

	public BusRouteNonMaximal find(Integer uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
