package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.model.Point;

public interface AddressServiceAdapter {

	List<String> getPossibleStreets(String token, String approximateStreetName);

	Point getByDoorNumber(String token, String firstStreet, Integer doorNumber,
			String letter);

	Point getCorner(String token, String firstStreet, String secondStreet);

	CloseLocationData getCloseLocation(String token, double latitude,
			double longitude);

}
