package model.dao;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Obstacle;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "ObstacleDAO")
public class ObstacleDAOImpl implements ObstacleDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	@EJB(name = "UserDAO")
	private UserDAO userDAO;

	public void add(Obstacle obstacle) {
		if (obstacle == null)
			throw new IllegalArgumentException("Obstacle cannot be null");
		if (find(obstacle.getCentre()) != null)
			throw new IllegalArgumentException(
					"Obstacle already exists in that position");
		if (obstacle.getCentre() == null || obstacle.getCreatedAt() == null
				|| obstacle.getUser() == null)
			throw new IllegalArgumentException(
					"Obstacle cannot contain null data");
		if (userDAO.find(obstacle.getUser().getUsername()) == null)
			throw new IllegalArgumentException("User does not exist");
		obstacle.setUser(userDAO.find(obstacle.getUser().getUsername()));
		entityManager.persist(obstacle);
	}

	public void delete(Obstacle obstacle) {
		if (obstacle == null)
			throw new IllegalArgumentException("obstacle cannot be null");
		Obstacle possible = find(obstacle.getCentre());
		if (possible == null)
			throw new IllegalArgumentException("obstacle does not exist");
		entityManager.remove(obstacle);
	}

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
	public List<Obstacle> findAll() {
		List<Obstacle> obstacles = null;
		try {
			Query q = entityManager.createNamedQuery("Obstacle.findAll");
			obstacles = q.getResultList();
			if (obstacles != null && obstacles.isEmpty()) {
				return null;
			}
		} catch (NoResultException e) {
			obstacles = null;
		}
		return obstacles;
	}

	@Override
	public void remove(Obstacle o) {
		if (o != null)
			entityManager.remove(o);
	}

	@Override
	public Obstacle findById(long id) {
		Obstacle o;
		try {
			Query q = entityManager.createNamedQuery("Obstacle.findById");
			q.setParameter("id", id);
			o = (Obstacle) q.getSingleResult();
		} catch (Exception e) {
			o = null;
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public long addObstacle(Obstacle o) {
		add(o);
		return o.getId();
	}

}
