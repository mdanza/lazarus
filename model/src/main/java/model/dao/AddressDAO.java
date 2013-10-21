package model.dao;

import javax.ejb.Local;

import model.Address;

@Local
public interface AddressDAO extends ModelDAO<Address, Integer> {

}
