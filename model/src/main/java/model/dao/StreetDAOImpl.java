package model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Street;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "StreetDAO")
public class StreetDAOImpl implements StreetDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(Street street) {
		/*
		 * if (street == null) throw new
		 * IllegalArgumentException("Street cannot be null"); MultiLineString
		 * segments = street.getSegments(); if (segments == null) throw new
		 * IllegalArgumentException("Segments cannot be null"); if
		 * (street.getName() == null) throw new
		 * IllegalArgumentException("name of street equals null"); if
		 * (street.getNameCode() == null) throw new
		 * IllegalArgumentException("nameCode of street equals null"); if
		 * (find(street.getName()) != null) throw new
		 * IllegalArgumentException("Street has already been saved");
		 */
		entityManager.persist(street);
	}

	public void delete(Street modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(Street streetOld, Street streetNew) {
		/*
		 * if (streetOld == null || streetNew == null) throw new
		 * IllegalArgumentException(
		 * "neither old street nor new street can be null"); Street
		 * possibleOldStreet = find(streetOld.getName()); if (possibleOldStreet
		 * == null) throw new
		 * IllegalArgumentException("Old street does not exist"); if
		 * (streetOld.getId() != streetNew.getId()) throw new
		 * IllegalArgumentException( "Ids of old and new streets do not match");
		 * if (streetOld.getName() == null ||
		 * !streetOld.getName().equals(streetNew.getName())) throw new
		 * IllegalArgumentException(
		 * "names of old and new streets do not match"); if
		 * (streetOld.getNameCode() == null ||
		 * !streetOld.getNameCode().equals(streetNew.getNameCode()))
		 * System.out.println
		 * ("nameCodes of old and new streets do not match from "
		 * +streetOld.getName()); MultiLineString segments =
		 * streetNew.getSegments(); if (segments == null) throw new
		 * IllegalArgumentException("StreetSegments cannot be null");
		 */
		entityManager.merge(streetNew);
	}

	public Street find(String name) {
		return null;
	}

	public List<Street> findByName(String name) {
		List<Street> streets;
		try {
			Query q = entityManager.createNamedQuery("Street.findByName");
			q.setParameter("name", name);
			streets = (List<Street>) q.getResultList();
		} catch (NoResultException e) {
			streets = null;
		}
		return streets;
	}

	public List<Street> findByNameCode(String nameCode) {
		List<Street> streets;
		try {
			Query q = entityManager.createNamedQuery("Street.findByNameCode");
			q.setParameter("nameCode", nameCode);
			streets = (List<Street>) q.getResultList();
		} catch (NoResultException e) {
			streets = null;
		}
		return streets;
	}

	public Street findClosestToPoint(Point point) {
		Street street;
		try {
			Query q = entityManager
					.createNamedQuery("Street.findClosestToPoint");
			// SessionFactory sessionFactory = new
			// Configuration().configure().buildSessionFactory();
			// Session session = sessionFactory.openSession();
			// Query q = (Query)
			// session.createSQLQuery("select streets FROM corners, streets WHERE corners.id='1' ORDER BY ST_Distance(corners.point, streets.segments) limit 1;").addEntity(Street.class);
			q.setParameter("point", point);
			q.setMaxResults(1);
			street = (Street) q.getSingleResult();
		} catch (NoResultException e) {
			street = null;
		}
		return street;
	}

	public List<String> findPossibleStreets(String approximate) {
		List<String> streets;
		try {
			Query q = entityManager
					.createNamedQuery("Street.findPossibleStreets");
			q.setParameter("name", approximate);
			streets = (List<String>) q.getResultList();
		} catch (NoResultException e) {
			streets = null;
		}
		return streets;
	}
	
	public List<Object[]> getStreetsMultiLinesEnd() {
		List<Object[]> boundary;
		try {
			Query q = entityManager
					.createNamedQuery("Street.findEndsOfMultiLines");
			boundary = (List<Object[]>) q.getResultList();
		} catch (NoResultException e) {
			boundary = null;
		}
		return boundary;
	}

	public void removeAll() {
		entityManager.createNamedQuery("Street.removeAll").executeUpdate();
	}

}
