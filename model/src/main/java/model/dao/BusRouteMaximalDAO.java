package model.dao;

import javax.ejb.Local;

import model.BusRouteMaximal;

@Local
public interface BusRouteMaximalDAO extends ModelDAO<BusRouteMaximal, Long> {
	public void removeAll();
}
