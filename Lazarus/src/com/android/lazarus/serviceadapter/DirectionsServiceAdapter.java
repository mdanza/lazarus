package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.BusRide;
import com.android.lazarus.model.Transshipment;
import com.android.lazarus.model.WalkingPosition;

public interface DirectionsServiceAdapter {

	/**
	 * @return bus ride directions in case of success, null if no directions or
	 *         invalid token
	 */
	public List<BusRide> getBusDirections(Double xOrigin, Double yOrigin,
			Double xEnd, Double yEnd, int distance, String token);

	/**
	 * @return bus transshipment directions in case of success, null if no
	 *         directions or invalid token
	 */
	public List<Transshipment> getBusDirectionsWithTransshipment(
			Double xOrigin, Double yOrigin, Double xEnd, Double yEnd,
			int distance, String token);
}
