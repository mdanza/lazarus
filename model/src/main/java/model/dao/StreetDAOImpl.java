package model.dao;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Role;
import model.Street;
import model.StreetSegment;
import model.User;

@Stateless(name = "StreetDAO")
public class StreetDAOImpl implements StreetDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	@EJB(name = "StreetSegmentDAO")
	private StreetSegmentDAO streetSegmentDAO;

	public void add(Street street) {
		/*
		if (street == null)
			throw new IllegalArgumentException("Street cannot be null");
		List<StreetSegment> segments = street.getStreetSegments();
		if (segments == null)
			throw new IllegalArgumentException("StreetSegments cannot be null");
		
		for (StreetSegment segment : segments) {
			if (streetSegmentDAO.findByOriginEnd(segment.getOrigin(), segment.getEnd()) == null)
				throw new IllegalArgumentException("streetSegment of street has not been saved yet");
		}
		if(find(street.getName())!=null)
			throw new IllegalArgumentException("Street has already been saved");
		if(street.getName()==null)
			throw new IllegalArgumentException("name of street equals null");
		if(street.getNameCode()==null)
			throw new IllegalArgumentException("nameCode of street equals null");
		*/
		System.out.println("Persisting street " + street.getName());
		entityManager.persist(street);
	}

	public void delete(Street modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(Street streetOld, Street streetNew) {
		/*
		if (streetOld == null || streetNew == null)
			throw new IllegalArgumentException(
					"neither old street nor new street can be null");
		Street possibleOldStreet = find(streetOld.getName());
		if (possibleOldStreet == null)
			throw new IllegalArgumentException("Old street does not exist");
		if (streetOld.getId() != streetNew.getId())
			throw new IllegalArgumentException(
					"Ids of old and new streets do not match");
		if (streetOld.getName()==null || !streetOld.getName().equals(streetNew.getName()))
			throw new IllegalArgumentException(
					"names of old and new streets do not match");
		//TODO I should check but throws exception?????
		
		if( streetOld.getNameCode()==null || !streetOld.getNameCode().equals(streetNew.getNameCode()))
			throw new IllegalArgumentException(
					"nameCodes of old and new streets do not match");
					
		List<StreetSegment> segments = streetNew.getStreetSegments();
		if (segments == null)
			throw new IllegalArgumentException("StreetSegments cannot be null");
		for (StreetSegment segment : segments) {
			if (streetSegmentDAO.findByOriginEnd(segment.getOrigin(), segment.getEnd()) == null)
				throw new IllegalArgumentException("streetSegment of street has not been saved yet");
		}
		*/
		entityManager.merge(streetNew);
	}

	public Street find(String name) {
		Street street;
		try {
			Query q = entityManager.createNamedQuery("Street.findByName");
			q.setParameter("name", name);
			street = (Street) q.getSingleResult();
		} catch (NoResultException e) {
			street = null;
		}
		return street;
	}

	public Street findByNameCode(String nameCode) {
		Street street;
		try {
			Query q = entityManager.createNamedQuery("Street.findByNameCode");
			q.setParameter("nameCode", nameCode);
			street = (Street) q.getSingleResult();
		} catch (NoResultException e) {
			street = null;
		}
		return street;
	}

}
