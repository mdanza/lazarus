package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
		// TODO Auto-generated method stub
		return null;
	}
}
