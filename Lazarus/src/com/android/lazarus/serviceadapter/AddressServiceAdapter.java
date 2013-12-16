package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.model.Point;

public interface AddressServiceAdapter {

	/**
	 * @return list of matches, null if none found or if invalid token
	 */
	public List<String> getPossibleStreets(String token,
			String approximateStreetName);

	/**
	 * @return address position, null if none found or if invalid token
	 */
	public Point getByDoorNumber(String token, String streetName,
			int doorNumber, String letter);

	/**
	 * @return corner position, null if none found or if invalid token
	 */
	public Point getCorner(String token, String firstStreet, String secondStreet);

	/**
	 * @return close location data, null if none found or if invalid token
	 */
	public CloseLocationData getCloseLocation(String token, String position);

}
