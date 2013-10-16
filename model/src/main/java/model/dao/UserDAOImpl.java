package model.dao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Role;
import model.User;

@Stateless(name = "UserDAO")
public class UserDAOImpl implements UserDAO {
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	@EJB(name = "RoleDAO")
	private RoleDAO roleDAO;

	public void add(User user) {
		if (user == null)
			throw new IllegalArgumentException("user cannot be null");
		String username = user.getUsername();
		String password = user.getPassword();
		String secretQuestion = user.getSecretQuestion();
		String secretAnswer = user.getSecretAnswer();
		String email = user.getEmail();
		String cellphone = user.getCellphone();
		Role role = user.getRole();
		if (username == null || username == "")
			throw new IllegalArgumentException(
					"username cannot be null nor empty");
		if (password == null || password == "")
			throw new IllegalArgumentException(
					"password cannot be null nor empty");
		if (secretAnswer == null || secretAnswer == "")
			throw new IllegalArgumentException(
					"secret answer cannot be null nor empty");
		if (secretQuestion == null || secretQuestion == "")
			throw new IllegalArgumentException(
					"secret question cannot be null nor empty");
		User possibleDuplicate = find(username);
		if (possibleDuplicate != null)
			throw new IllegalArgumentException("username already exists");
		if (email != null) {
			possibleDuplicate = findByEmail(email);
			if (possibleDuplicate != null)
				throw new IllegalArgumentException("email already exists");
		}
		if (cellphone != null) {
			possibleDuplicate = findByCellphone(cellphone);
			if (possibleDuplicate != null)
				throw new IllegalArgumentException("cellphone already exists");
		}
		if (role == null)
			throw new IllegalArgumentException("role cannot be null");
		Role possibleRole = roleDAO.find(role.getName());
		if (possibleRole == null)
			throw new IllegalArgumentException("role is not valid");
		user.setRole(possibleRole);
		entityManager.persist(user);
	}

	public void delete(User user) {
		if (user == null)
			throw new IllegalArgumentException("user cannot be null");
		User possibleUser = find(user.getUsername());
		if (possibleUser == null)
			throw new IllegalArgumentException("user does not exist");
		entityManager.remove(possibleUser);
	}

	public void modify(User userOld, User userNew) {
		if (userOld == null || userNew == null)
			throw new IllegalArgumentException(
					"neither old user nor new user can be null");
		User possibleOldUser = find(userOld.getUsername());
		if (possibleOldUser == null)
			throw new IllegalArgumentException("Old user does not exist");
		if (userOld.getId() != userNew.getId()
				|| userOld.getUsername() != userNew.getUsername())
			throw new IllegalArgumentException(
					"Ids or usernames of old and new user do not match");
		Role role = userNew.getRole();
		if (role == null)
			throw new IllegalArgumentException(
					"role of new user cannot be null");
		Role possibleRole = roleDAO.find(role.getName());
		if (possibleRole == null)
			throw new IllegalArgumentException("role of new user is not valid");
		userNew.setRole(possibleRole);
		entityManager.merge(userNew);
	}

	public User find(String username) {
		User user;
		try {
			Query q = entityManager.createNamedQuery("User.findByUsername");
			q.setParameter("username", username);
			user = (User) q.getSingleResult();
		} catch (NoResultException e) {
			user = null;
		}
		return user;
	}

	public User findByEmail(String email) {
		User user;
		try {
			Query q = entityManager.createNamedQuery("User.findByEmail");
			q.setParameter("email", email);
			user = (User) q.getSingleResult();
		} catch (NoResultException e) {
			user = null;
		}
		return user;
	}

	public User findByCellphone(String cellphone) {
		User user;
		try {
			Query q = entityManager.createNamedQuery("User.findByCellphone");
			q.setParameter("cellphone", cellphone);
			user = (User) q.getSingleResult();
		} catch (NoResultException e) {
			user = null;
		}
		return user;
	}

}
