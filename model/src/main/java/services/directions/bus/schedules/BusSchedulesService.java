package services.directions.bus.schedules;

import javax.ejb.Local;

import model.Bus;

@Local
public interface BusSchedulesService {
	/**
	 * 
	 * @param variantCode bus line variante code
	 * @param subLineCode bus line subline code
	 * @param maximumBusStopOrdinal maximum ordinal of already passed bus stop
	 */
	public Bus getClosestBus(int variantCode, int subLineCode, int maximumBusStopOrdinal);
	
	/**
	 * 
	 * @param variantCode bus line variante code
	 * @param subLineCode bus line subline code
	 * @param maximumBusStopLocationCode bus stop for which you want the schedule
	 * @return ordered array of times represented as minutes since start of day. Example: 10:40 => 10*60 + 40 = 640
	 */
	public int[] getBusLineSchedule(int variantCode, int subLineCode, int busStopLocationCode);
}
