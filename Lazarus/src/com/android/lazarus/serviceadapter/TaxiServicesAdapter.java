package com.android.lazarus.serviceadapter;

import java.util.List;

import com.android.lazarus.model.TaxiService;

public interface TaxiServicesAdapter {

	public List<TaxiService> getAllTaxiServices(String token);
}
