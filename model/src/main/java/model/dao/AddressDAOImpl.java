package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Address;

@Stateless(name = "AddressDAO")
public class AddressDAOImpl implements AddressDAO {

	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	public void add(Address address) {
		// TODO Auto-generated method stub
		entityManager.persist(address);

	}

	public void delete(Address address) {
		// TODO Auto-generated method stub

	}

	public void modify(Address oldAddress, Address newAddress) {
		// TODO Auto-generated method stub

	}

	public Address find(Long uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public Address findByStreetNameAndNumber(String streetName, long number,
			String letter) {
		Address address;
		try {
			Query q = entityManager
					.createNamedQuery("Address.findByStreetNameAndNumber");
			q.setParameter("streetName", streetName);
			q.setParameter("number", number);
			q.setParameter("letter", letter);
			address = (Address) q.getSingleResult();
		} catch (NoResultException e) {
			address = null;
		}
		return address;
	}

}
