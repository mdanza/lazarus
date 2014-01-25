package model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.RouteTitle;

@Stateless(name = "RouteTitleDAO")
public class RouteTitleDAOImpl implements RouteTitleDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(RouteTitle modelObject) {
		entityManager.persist(modelObject);
	}

	public void delete(RouteTitle modelObject) {
		// TODO Auto-generated method stub

	}

	public void modify(RouteTitle modelObjectOld, RouteTitle modelObjectNew) {
		// TODO Auto-generated method stub

	}

	public RouteTitle find(String uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void removeAll() {
		entityManager.createNamedQuery("RouteTitle.removeAll").executeUpdate();
	}
	
	public List<RouteTitle> getAll() {
		List<RouteTitle> routeTitles;
		try {
			Query q = entityManager.createNamedQuery("RouteTitle.findAll");
			routeTitles = (List<RouteTitle>) q.getResultList();
		} catch (NoResultException e) {
			routeTitles = null;
		}
		return routeTitles;
	}

}

