package model.dao;

import model.Street;

public interface StreetDAO extends ModelDAO<Street, String> {
	
	public Street findByNameCode(String nameCode);
	
	

}
