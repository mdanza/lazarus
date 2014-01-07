package services.incidents.busstops;

import java.util.List;

import javax.ejb.Local;

import model.BusStop;

@Local
public interface BusStopService {
	public void activateStops(long locationCode);

	public void deactivateStops(long locationCode);

	public List<BusStop> findAllDistinctLocationCodes();

	public List<BusStop> getLineStops(long variantCode);
}
