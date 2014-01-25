package model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.RouteType;


@Stateless(name = "RouteTypeDAO")
public class RouteTypeDAOImpl implements RouteTypeDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(RouteType modelObject) {
		entityManager.persist(modelObject);
	}

	public void delete(RouteType modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(RouteType modelObjectOld, RouteType modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public RouteType find(String uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void removeAll() {
		entityManager.createNamedQuery("RouteType.removeAll").executeUpdate();
	}
	
	public List<RouteType> getAll() {
		List<RouteType> routeTypes;
		try {
			Query q = entityManager.createNamedQuery("RouteType.findAll");
			routeTypes = (List<RouteType>) q.getResultList();
		} catch (NoResultException e) {
			routeTypes = null;
		}
		return routeTypes;
	}

}
