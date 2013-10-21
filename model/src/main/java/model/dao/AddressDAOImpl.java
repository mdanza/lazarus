package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

	public Address find(Integer uniqueKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
