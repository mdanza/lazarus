package com.android.lazarus.serviceadapter;

import java.util.ArrayList;
import java.util.List;


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

}
