package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Position;
import model.Street;
import model.StreetSegment;

@Stateless(name = "StreetSegmentDAO")
public class StreetSegmentDAOImpl implements StreetSegmentDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(StreetSegment streetSegment) {
		if (streetSegment == null)
			throw new IllegalArgumentException("street segment cannot be null");
		Position origin = streetSegment.getOrigin();
		Position end = streetSegment.getEnd();
		if (origin == null)
			throw new IllegalArgumentException("Origin cannot be null");
		if (end == null)
			throw new IllegalArgumentException("End cannot be null");
		entityManager.persist(origin);
		entityManager.persist(end);
		entityManager.persist(streetSegment);
	}

	public void delete(StreetSegment modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(StreetSegment modelObjectOld,
			StreetSegment modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public StreetSegment find(String id) {
		StreetSegment streetSegment;
		try {
			Query q = entityManager.createNamedQuery("StreetSegment.findById");
			q.setParameter("id", Integer.getInteger(id));
			streetSegment = (StreetSegment) q.getSingleResult();
		} catch (NoResultException e) {
			streetSegment = null;
		}
		return streetSegment;		
	}

}
