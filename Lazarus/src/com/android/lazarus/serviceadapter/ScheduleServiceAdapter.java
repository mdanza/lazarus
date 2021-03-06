package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.Bus;

public interface ScheduleServiceAdapter {

	/**
	 * @return schedule in format hh:mm:ss or null in case of error
	 */
	public List<String> getBusSchedule(String token, String lineName,
			String subLineDescription, long busStopLocationCode,
			int minutesSinceStartOfDay);

	/**
	 * @return closest found bus or null
	 */
	public Bus getClosestBus(String token, long variantCode, long subLineCode,
			long busStopOrdinal);
}
