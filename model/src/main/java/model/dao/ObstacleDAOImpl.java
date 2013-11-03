package model.dao;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Corner;
import model.Obstacle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "ObstacleDAO")
public class ObstacleDAOImpl implements ObstacleDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	@EJB(name = "UserDAO")
	private UserDAO userDAO;

	@Override
	public void add(Obstacle obstacle) {
		if (obstacle == null)
			throw new IllegalArgumentException("Obstacle cannot be null");
		if (find(obstacle.getCentre()) != null)
			throw new IllegalArgumentException(
					"Obstacle already exists in that position");
		if (obstacle.getCentre() == null || obstacle.getCircle() == null
				|| obstacle.getCreatedAt() == null
				|| obstacle.getUser() == null)
			throw new IllegalArgumentException(
					"Obstacle cannot contain null data");
		if (userDAO.find(obstacle.getUser().getUsername()) == null)
			throw new IllegalArgumentException("User does not exist");
		obstacle.setUser(userDAO.find(obstacle.getUser().getUsername()));
		entityManager.persist(obstacle);
	}

	@Override
	public void delete(Obstacle obstacle) {
		if (obstacle == null)
			throw new IllegalArgumentException("obstacle cannot be null");
		Obstacle possible = find(obstacle.getCentre());
		if (possible == null)
			throw new IllegalArgumentException("obstacle does not exist");
		entityManager.remove(obstacle);
	}

	@Override
	public void modify(Obstacle obstacleOld, Obstacle obstacleNew) {
		if (obstacleOld == null || obstacleNew == null)
			throw new IllegalArgumentException("obtacles cannot be null");
		Obstacle possibleOld = find(obstacleOld.getCentre());
		if (possibleOld == null)
			throw new IllegalArgumentException("Old obstacle does not exist");
		if (obstacleOld.getId() != obstacleNew.getId()
				|| obstacleOld.getCentre() != obstacleNew.getCentre())
			throw new IllegalArgumentException(
					"Ids or centres of old and new obstacles do not match");
		if (obstacleNew.getUser() == null)
			throw new IllegalArgumentException(
					"user of new obstacle cannot be null");
		if (userDAO.find(obstacleNew.getUser().getUsername()) == null)
			throw new IllegalArgumentException(
					"user of new obstacle does not exist");
		obstacleNew.setUser(userDAO.find(obstacleNew.getUser().getUsername()));
		entityManager.merge(obstacleNew);

	}

	@Override
	public Obstacle find(Point centre) {
		Obstacle obstacle;
		try {
			Query q = entityManager.createNamedQuery("Obstacle.findByCentre");
			q.setParameter("centre", centre);
			obstacle = (Obstacle) q.getSingleResult();
		} catch (NoResultException e) {
			obstacle = null;
		}
		return obstacle;
	}
	
	@Override
	public List<Obstacle> findByDistance(Geometry geometry,
			Double distance) {
		List<Obstacle> obstacles = null;
		try {
			Query q = entityManager.createNamedQuery("Obstacle.findByDistance");
			GeometryFactory factory = new GeometryFactory();
			q.setParameter("geometry", geometry);
			q.setParameter("distance", distance);
			obstacles = (List<Obstacle>) q.getResultList();
			if(obstacles!=null && obstacles.isEmpty()){
				return null;
			}
		} catch (NoResultException e) {
			obstacles = null;
		}
		return obstacles;	
	}

}
