package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
		entityManager.merge(modelObjectNew);
	}

	public Bus find(Integer id) {
		Bus bus;
		try {
			Query q = entityManager.createNamedQuery("Bus.findById");
			q.setParameter("id", id);
			bus = (Bus) q.getSingleResult();
		} catch (NoResultException e) {
			bus = null;
		}
		return bus;
	}

}
