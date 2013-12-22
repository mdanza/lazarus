package model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Corner;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

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

	public Corner find(Long uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Corner> findCornersWithinRadius(Point point, Double radius) {
		List<Corner> corners;
		try {
			Query q = entityManager.createNamedQuery("Corner.findWithinRadius");
			// SessionFactory sessionFactory = new
			// Configuration().configure().buildSessionFactory();
			// Session session = sessionFactory.openSession();
			// Query q = (Query)
			// session.createSQLQuery("select streets FROM corners, streets WHERE corners.id='1' ORDER BY ST_Distance(corners.point, streets.segments) limit 1;").addEntity(Street.class);
			GeometryFactory factory = new GeometryFactory();
			q.setParameter("point", point);
			q.setParameter("radius", radius);
			corners = (List<Corner>) q.getResultList();
		} catch (NoResultException e) {
			corners = null;
		}
		return corners;
	}

	public List<Corner> findByStreetNames(String mainStreet, String cornerStreet) {
		List<Corner> corner;
		try {
			Query q = entityManager
					.createNamedQuery("Corner.findByStreetNames");
			q.setParameter("firstStreetName", mainStreet);
			q.setParameter("secondStreetName", cornerStreet);
			corner = (List<Corner>) q.getResultList();
		} catch (NoResultException e) {
			corner = null;
		}
		return corner;
	}

	public Corner findClosestToPoint(Point point) {
		Corner corner;
		try {
			Query q = entityManager
					.createNamedQuery("Corner.findClosestToPoint");
			q.setParameter("point", point);
			q.setMaxResults(1);
			corner = (Corner) q.getSingleResult();
		} catch (NoResultException e) {
			corner = null;
		}
		return corner;
	}

	public void removeAll() {
		entityManager.createNamedQuery("Corner.removeAll").executeUpdate();
	}
}
