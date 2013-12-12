package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.model.Point;

public class AddressServiceAdapterImpl implements AddressServiceAdapter{

	@Override
	public List<String> getPossibleStreets(String approximateStreetName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getByDoorNumber(String firstStreet, Integer doorNumber,
			String letter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getCorner(String firstStreet, String secondStreet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CloseLocationData getCloseLocation(double latitude, double longitude) {
		// TODO Auto-generated method stub
		return null;
	}

}
