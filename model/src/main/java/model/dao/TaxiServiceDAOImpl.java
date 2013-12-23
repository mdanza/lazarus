package model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.TaxiService;

@Stateless(name = "TaxiServiceDAO")
public class TaxiServiceDAOImpl implements TaxiServiceDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	@Override
	public void add(TaxiService modelObject) {
		if (modelObject == null || modelObject.getName() == null
				|| modelObject.getPhone() == null)
			throw new IllegalArgumentException(
					"TaxiService cannot be null or have null name or null number");
		if (find(modelObject.getName()) != null)
			throw new IllegalArgumentException("TaxiService already exists");
		entityManager.persist(modelObject);
	}

	@Override
	public void delete(TaxiService modelObject) {
		if (modelObject == null)
			throw new IllegalArgumentException("TaxiService cannot be null");
		if (find(modelObject.getName()) == null)
			throw new IllegalArgumentException("TaxiService does not exist");
		entityManager.remove(modelObject);
	}

	@Override
	public void modify(TaxiService modelObjectOld, TaxiService modelObjectNew) {
		// TODO Auto-generated method stub

	}

	@Override
	public TaxiService find(String name) {
		TaxiService result;
		try {
			Query q = entityManager.createNamedQuery("TaxiService.findByName");
			q.setParameter("name", name);
			result = (TaxiService) q.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}

	@Override
	public List<TaxiService> findAll() {
		List<TaxiService> result;
		try {
			result =  entityManager.createNamedQuery("TaxiService.findByName").getResultList();
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

}
