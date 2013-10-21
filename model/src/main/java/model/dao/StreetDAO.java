package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.Street;

@Local
public interface StreetDAO extends ModelDAO<Street, String> {
	
	public List<Street> findByNameCode(String nameCode);
	
	public List<Street> findByName(String nameCode);

}
