package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.TaxiService;

@Local
public interface TaxiServiceDAO extends ModelDAO<TaxiService, String> {

	public List<TaxiService> findAll();
	
	public void removeAll();
}
