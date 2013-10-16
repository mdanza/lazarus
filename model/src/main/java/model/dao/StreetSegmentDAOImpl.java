package model.dao;

import javax.ejb.EJB;
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
	
	@EJB(name = "PositionDAO")
	private PositionDAO positionDAO;

	public void add(StreetSegment streetSegment) {
		if (streetSegment == null)
			throw new IllegalArgumentException("street segment cannot be null");
		Position origin = streetSegment.getOrigin();
		Position end = streetSegment.getEnd();
		if (origin == null)
			throw new IllegalArgumentException("Origin cannot be null");
		if (end == null)
			throw new IllegalArgumentException("End cannot be null");
		if(positionDAO.findByLatitudeLongitude(origin.getLatitude(), origin.getLongitude())==null)
			throw new IllegalArgumentException("Origin has not been previously saved");
		if(positionDAO.findByLatitudeLongitude(end.getLatitude(), end.getLongitude())==null)
			throw new IllegalArgumentException("End has not been previously saved");
		if(findByOriginEnd(origin, end)!=null)
			throw new IllegalArgumentException("StreetSegment has already been saved");
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
		throw new IllegalArgumentException("you shouldn't have called me");
	}

	public StreetSegment findByOriginEnd(Position origin, Position end) {
		StreetSegment streetSegment;
		try {
			Query q = entityManager.createNamedQuery("StreetSegment.findByOriginEnd");
			q.setParameter("origin", origin);
			q.setParameter("end", end);
			streetSegment = (StreetSegment) q.getSingleResult();
		} catch (NoResultException e) {
			streetSegment = null;
		}
		return streetSegment;	
	}

}
