package com.android.lazarus.serviceadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.SerializationHelper;
import com.android.lazarus.model.Bus;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BusReportingServiceAdapterImpl implements
		BusReportingServiceAdapter {

	@Override
	public Bus getBus(String token, long busId) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL + "/bus/"
				+ busId);
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonBus = jsonResponse.get("data").getAsString();
				Bus bus = SerializationHelper.gsonInvertedCoords.fromJson(jsonBus, Bus.class);
				return bus;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
