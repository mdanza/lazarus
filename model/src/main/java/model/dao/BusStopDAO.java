package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.BusStop;

@Local
public interface BusStopDAO extends ModelDAO<BusStop, Long> {

	public List<BusStop> getLineStops(long variantCode);

	public void removeAll();
}
