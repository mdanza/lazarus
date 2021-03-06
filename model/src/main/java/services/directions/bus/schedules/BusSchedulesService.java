package services.directions.bus.schedules;

import java.util.List;

import javax.ejb.Local;

import model.Bus;

@Local
public interface BusSchedulesService {
	/**
	 * 
	 * @param variantCode
	 *            bus line variante code
	 * @param subLineCode
	 *            bus line subline code
	 * @param maximumBusStopOrdinal
	 *            maximum ordinal of already passed bus stop
	 */
	public Bus getClosestBus(long variantCode, long subLineCode,
			long maximumBusStopOrdinal);

	public List<String> getBusLineSchedule(String lineName,
			String subLineDescription, long busStopLocationCode,
			int fromMinutesSinceStartOfDay);
}
