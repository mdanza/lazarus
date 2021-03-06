package com.android.lazarus.serviceadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.SerializationHelper;
import com.android.lazarus.model.Bus;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ScheduleServiceAdapterImpl implements ScheduleServiceAdapter {

	@Override
	public List<String> getBusSchedule(String token, String lineName,
			String subLineDescription, long busStopLocationCode,
			int minutesSinceStartOfDay) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		try {
			HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL
					+ "/schedule?" + "lineName=" + lineName
					+ "&subLineDescription="
					+ URLEncoder.encode(subLineDescription, "UTF-8")
					+ "&busStopLocationCode=" + busStopLocationCode
					+ "&minutesSinceStartOfDay=" + minutesSinceStartOfDay);
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonSchedule = jsonResponse.get("data").getAsString();
				Type type = new TypeToken<List<String>>() {
				}.getType();
				List<String> schedule = SerializationHelper.gsonInvertedCoords.fromJson(
						jsonSchedule, type);
				return schedule;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Bus getClosestBus(String token, long variantCode, long subLineCode,
			long busStopOrdinal) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL
				+ "/schedule/bus?variantCode=" + variantCode + "&subLineCode="
				+ subLineCode + "&busStopOrdinal=" + busStopOrdinal);
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
