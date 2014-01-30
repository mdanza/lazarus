package services.incidents.busstops;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.BusStop;
import model.dao.BusStopDAO;

@Stateless(name = "BusStopService")
public class BusStopServiceImpl implements BusStopService {
	@EJB(beanName = "BusStopDAO")
	private BusStopDAO busStopDAO;

	@Override
	public void activateStops(long locationCode) {
		busStopDAO.changeActiveFieldByLocationCode(locationCode, true);
	}

	@Override
	public void deactivateStops(long locationCode) {
		busStopDAO.changeActiveFieldByLocationCode(locationCode, false);
	}

	@Override
	public List<BusStop> findAllDistinctLocationCodes(int page) {
		return busStopDAO.getDistinctLocationCodeBusStops(page);
	}

	@Override
	public List<BusStop> getLineStops(long variantCode) {
		return busStopDAO.getLineStops(variantCode);
	}

}
