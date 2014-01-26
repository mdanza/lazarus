package com.android.lazarus.serviceadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.SerializationHelper;
import com.android.lazarus.model.TaxiService;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class TaxiServicesAdapterImpl implements TaxiServicesAdapter {

	@Override
	public List<TaxiService> getAllTaxiServices(String token) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL + "/taxis");
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				Type type = new TypeToken<List<TaxiService>>() {
				}.getType();
				List<TaxiService> taxis = SerializationHelper.gson.fromJson(
						jsonResponse.get("data").getAsString(), type);
				if (taxis != null && taxis.size() > 0)
					return taxis;
				else
					return null;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
