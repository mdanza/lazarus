package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.RouteType;

@Local
public interface RouteTypeDAO extends ModelDAO<RouteType, String> {

	public void removeAll();

	public List<RouteType> getAll();

}
