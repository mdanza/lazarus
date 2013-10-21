package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Street;

import com.vividsolutions.jts.geom.MultiLineString;

@Stateless(name = "StreetDAO")
public class StreetDAOImpl implements StreetDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(Street street) {
		if (street == null)
			throw new IllegalArgumentException("Street cannot be null");
		MultiLineString segments = street.getSegments();
		if (segments == null)
			throw new IllegalArgumentException("Segments cannot be null");
		if (street.getName() == null)
			throw new IllegalArgumentException("name of street equals null");
		if (street.getNameCode() == null)
			throw new IllegalArgumentException("nameCode of street equals null");
		if (find(street.getName()) != null)
			throw new IllegalArgumentException("Street has already been saved");
		System.out.println("Persisting street " + street.getName());
		entityManager.persist(street);
	}

	public void delete(Street modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(Street streetOld, Street streetNew) {
		if (streetOld == null || streetNew == null)
			throw new IllegalArgumentException(
					"neither old street nor new street can be null");
		Street possibleOldStreet = find(streetOld.getName());
		if (possibleOldStreet == null)
			throw new IllegalArgumentException("Old street does not exist");
		if (streetOld.getId() != streetNew.getId())
			throw new IllegalArgumentException(
					"Ids of old and new streets do not match");
		if (streetOld.getName() == null
				|| !streetOld.getName().equals(streetNew.getName()))
			throw new IllegalArgumentException(
					"names of old and new streets do not match");
		if (streetOld.getNameCode() == null
				|| !streetOld.getNameCode().equals(streetNew.getNameCode()))
			throw new IllegalArgumentException(
					"nameCodes of old and new streets do not match");
		MultiLineString segments = streetNew.getSegments();
		if (segments == null)
			throw new IllegalArgumentException("StreetSegments cannot be null");
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
