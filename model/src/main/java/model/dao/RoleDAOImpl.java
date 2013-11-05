package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Role;

@Stateless(name = "RoleDAO")
public class RoleDAOImpl implements RoleDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(Role role) {
		if (role == null)
			throw new IllegalArgumentException("role cannot be null");
		String name = role.getName();
		if (name == null || name == "")
			throw new IllegalArgumentException("name cannot be null nor empty");
		Role possibleDuplicate = find(name);
		if (possibleDuplicate != null)
			throw new IllegalArgumentException("name already exists");
		entityManager.persist(role);
	}

	public void delete(Role role) {
		if (role == null)
			throw new IllegalArgumentException("role cannot be null");
		Role possibleRole = find(role.getName());
		if (possibleRole == null)
			throw new IllegalArgumentException("role does not exist");
		entityManager.remove(role);
	}

	public void modify(Role roleOld, Role roleNew) {
		if (roleOld == null || roleNew == null)
			throw new IllegalArgumentException(
					"neither old role nor new role can be null");
		Role possibleOldRole = find(roleOld.getName());
		if (possibleOldRole == null)
			throw new IllegalArgumentException("Old role does not exist");
		if (roleOld.getId() != roleNew.getId()
				|| roleOld.getName() != roleNew.getName())
			throw new IllegalArgumentException(
					"Ids or names of old and new role do not match");
		entityManager.merge(roleNew);
	}

	public Role find(String name) {
		Role role;
		try {
			Query q = entityManager.createNamedQuery("Role.findByName");
			q.setParameter("name", name);
			role = (Role) q.getSingleResult();
		} catch (NoResultException e) {
			role = null;
		}
		return role;
	}
}
