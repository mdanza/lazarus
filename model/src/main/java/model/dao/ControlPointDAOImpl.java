package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.ControlPoint;

@Stateless(name = "ControlPointDAO")
public class ControlPointDAOImpl implements ControlPointDAO {
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	@Override
	public void add(ControlPoint modelObject) {
		entityManager.persist(modelObject);
	}

	@Override
	public void delete(ControlPoint modelObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modify(ControlPoint modelObjectOld, ControlPoint modelObjectNew) {
		// TODO Auto-generated method stub
	}

	@Override
	public ControlPoint find(Long id) {
		ControlPoint controlPoint;
		try {
			Query q = entityManager.createNamedQuery("ControlPoint.findById");
			q.setParameter("id", id);
			controlPoint = (ControlPoint) q.getSingleResult();
		} catch (NoResultException e) {
			controlPoint = null;
		}
		return controlPoint;
	}

	@Override
	public void removeAll() {
		entityManager.createNamedQuery("ControlPoint.removeAll")
				.executeUpdate();
	}

}
