package model.dao;

import javax.ejb.Local;

import model.Street;

@Local
public interface StreetDAO extends ModelDAO<Street, String> {
	
	public Street findByNameCode(String nameCode);
	
	

}
