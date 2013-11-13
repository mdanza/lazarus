package model.dao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Favourite;
import model.Obstacle;
import model.User;

@Stateless(name = "FavouriteDAO")
public class FavouriteDAOImpl implements FavouriteDAO {

		@PersistenceContext(unitName = "lazarus-persistence-unit")
		private EntityManager entityManager;

		@EJB(name = "UserDAO")
		private UserDAO userDAO;

		@Override
		public void add(Favourite favourite) {
			if(favourite==null)
				throw new IllegalArgumentException("favourite is null");
			if(favourite.getName()==null || favourite.getUser()==null || favourite.getPoint()==null)
				throw new IllegalArgumentException("name, user or point equals null");
			entityManager.persist(favourite);
		}

		@Override
		public void delete(Favourite favourite) {
			if(favourite==null)
				throw new IllegalArgumentException("favourite is null");
			Favourite old = findByUserAndName(favourite.getUser(),favourite.getName());
			if(old==null)
				throw new IllegalArgumentException("Favourite does not exist");				
			entityManager.remove(old);
		}

		@Override
		public void modify(Favourite modelObjectOld, Favourite modelObjectNew) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Favourite find(String uniqueKey) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
		@Override
		public Favourite findByUserAndName(User user, String name) {
			if(user==null || name == null)
				throw new IllegalArgumentException("Null user or name");
			Favourite favourite;
			try {
				Query q = entityManager.createNamedQuery("Favourite.findByUserAndName");
				q.setParameter("username", user.getUsername());
				q.setParameter("name", name);
				favourite = (Favourite) q.getSingleResult();
			} catch (NoResultException e) {
				favourite = null;
			}
			return favourite;
		}
		
		
		

}
