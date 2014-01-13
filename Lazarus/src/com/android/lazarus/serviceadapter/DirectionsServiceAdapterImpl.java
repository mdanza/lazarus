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
import com.android.lazarus.model.BusRide;
import com.android.lazarus.model.Transshipment;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class DirectionsServiceAdapterImpl implements DirectionsServiceAdapter {

	@Override
	public List<BusRide> getBusDirections(Double xOrigin, Double yOrigin,
			Double xEnd, Double yEnd, int distance, String token) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL
				+ "/directions/busDirections?xOrigin=" + xOrigin + "&yOrigin="
				+ yOrigin + "&xEnd=" + xEnd + "&yEnd=" + yEnd + "&distance"
				+ distance);
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonDirections = jsonResponse.get("data").getAsString();
				Type type = new TypeToken<List<BusRide>>() {
				}.getType();
				List<BusRide> directions = SerializationHelper.gson.fromJson(
						jsonDirections, type);
				return directions;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Transshipment> getBusDirectionsWithTransshipment(
			Double xOrigin, Double yOrigin, Double xEnd, Double yEnd,
			int distance, String token) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL
				+ "/directions/busDirectionsWithTransshipment?xOrigin="
				+ xOrigin + "&yOrigin=" + yOrigin + "&xEnd=" + xEnd + "&yEnd="
				+ yEnd + "&distance" + distance);
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonDirections = jsonResponse.get("data").getAsString();
				Type type = new TypeToken<List<Transshipment>>() {
				}.getType();
				List<Transshipment> directions = SerializationHelper.gson
						.fromJson(jsonDirections, type);
				return directions;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
