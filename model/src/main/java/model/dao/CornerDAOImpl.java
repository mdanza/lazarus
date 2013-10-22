package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.Address;
import model.Corner;

@Stateless(name = "CornerDAO")
public class CornerDAOImpl implements CornerDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(Corner corner) {
		entityManager.persist(corner);

	}

	public void delete(Corner modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(Corner modelObjectOld, Corner modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public Corner find(Integer uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
