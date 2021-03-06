package model.dao;

import javax.ejb.Local;

import model.Address;

@Local
public interface AddressDAO extends ModelDAO<Address, Long> {

	public Address findByStreetNameAndNumber(String streetName, long number,
			String letter);

	public void removeAll();
}
