package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Bus;

@Stateless(name = "BusDAO")
public class BusDAOImpl implements BusDAO {
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(Bus modelObject) {
		entityManager.persist(modelObject);
	}

	public void delete(Bus modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(Bus modelObjectOld, Bus modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public Bus find(Integer uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
