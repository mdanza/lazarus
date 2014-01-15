package com.android.lazarus.serviceadapter;

import com.android.lazarus.model.Bus;

public interface BusReportingServiceAdapter {

	/**
	 * 
	 * @return bus with given id, null if none found
	 */
	public Bus getBus(String token, int busId);
}
