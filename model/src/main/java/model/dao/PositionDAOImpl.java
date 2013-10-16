package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Position;

@Stateless(name = "PositionDAO")
public class PositionDAOImpl implements PositionDAO {
	
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(Position position) {
		if(position==null)
			throw new IllegalArgumentException("Position cannot be null");
		if(findByLatitudeLongitude(position.getLatitude(), position.getLongitude())!=null)
			throw new IllegalArgumentException("Position already exists in DB");
		entityManager.persist(position);

	}

	public void delete(Position modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(Position modelObjectOld, Position modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public Position find(String uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Position findByLatitudeLongitude(double latitude, double longitude) {
		Position position;
		try {
			Query q = entityManager.createNamedQuery("Position.findByLatitudeLongitude");
			q.setParameter("latitude", latitude);
			q.setParameter("longitude", longitude);
			position = (Position) q.getSingleResult();
		} catch (NoResultException e) {
			position = null;
		}
		return position;
	}
	
}
