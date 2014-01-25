package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.RouteTitle;

@Local
public interface RouteTitleDAO  extends ModelDAO<RouteTitle, String> {
	
	public void removeAll();
	
	public List<RouteTitle> getAll();

}