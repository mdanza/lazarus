package com.android.lazarus.serviceadapter;

import java.util.ArrayList;
import java.util.List;

import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.model.Point;


public class AddressServiceAdapterStub implements AddressServiceAdapter {

	@Override
	public List<String> getPossibleStreets(String approximateStreetName) {
		if("rivera".equals(approximateStreetName)){
			ArrayList<String> streets = new ArrayList<String>();
			streets.add("AV GRAL RIVERA");
			streets.add("PUTO");
			return streets;
		}else{
			return null;
		}
	}

	@Override
	public Point getByDoorNumber(String firstStreet, Integer doorNumber,
			String letter) {
		return new Point();
	}

	@Override
	public Point getCorner(String firstStreet, String secondStreet) {
		return new Point();
	}

	@Override
	public CloseLocationData getCloseLocation(double latitude, double longitude) {
		return new CloseLocationData();
	}

}
