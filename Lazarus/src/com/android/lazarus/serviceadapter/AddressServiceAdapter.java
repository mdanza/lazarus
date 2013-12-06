package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.model.Point;

public interface AddressServiceAdapter {

	List<String> getPossibleStreets(String approximateStreetName);

	Point getByDoorNumber(String firstStreet, Integer doorNumber, String letter);

	Point getCorner(String firstStreet, String secondStreet);

	CloseLocationData getCloseLocation(double latitude, double longitude);
	
	
}
