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
import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.model.Point;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class AddressServiceAdapterImpl implements AddressServiceAdapter {

	@Override
	public List<String> getPossibleStreets(String token, String name) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		try {
			String url = ConstantsHelper.REST_API_URL
					+ "/addresses/possibleStreets?name=" + URLEncoder.encode(name, ConstantsHelper.ENCODING);
			HttpGet request = new HttpGet(url);
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonPossibleStreets = jsonResponse.get("data")
						.getAsString();
				Type type = new TypeToken<List<String>>() {
				}.getType();
				List<String> possibleStreets = SerializationHelper.gson
						.fromJson(jsonPossibleStreets, type);
				return possibleStreets;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Point getByDoorNumber(String token, String firstStreet,
			int doorNumber, String letter) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		try {
			String uri = ConstantsHelper.REST_API_URL
					+ "/addresses/addressNumberToCoordinates?streetName="
					+ URLEncoder.encode(firstStreet, ConstantsHelper.ENCODING) + "&number=" + URLEncoder.encode(Integer.toString(doorNumber), ConstantsHelper.ENCODING) + "&letter="
					+ URLEncoder.encode(letter, ConstantsHelper.ENCODING);
			HttpGet request = new HttpGet(uri);
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonPoint = jsonResponse.get("data").getAsString();
				Point point = SerializationHelper.gson.fromJson(jsonPoint,
						Point.class);
				return point;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Point getCorner(String token, String mainStreet, String cornerStreet) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		try {
			String uri = ConstantsHelper.REST_API_URL
					+ "/addresses/cornerToCoordinates?mainStreet=" + URLEncoder.encode(mainStreet, ConstantsHelper.ENCODING)
					+ "&cornerStreet=" + URLEncoder.encode(cornerStreet, ConstantsHelper.ENCODING);
			HttpGet request = new HttpGet(uri);
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonPoint = jsonResponse.get("data").getAsString();
				Point point = SerializationHelper.gson.fromJson(jsonPoint,
						Point.class);
				return point;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CloseLocationData getCloseLocation(String token, String position) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		try {
			String uri = ConstantsHelper.REST_API_URL
					+ "/addresses/closeData?position=" + URLEncoder.encode(position, ConstantsHelper.ENCODING);
			HttpGet request = new HttpGet(uri);
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonCloseLocationData = jsonResponse.get("data")
						.getAsString();
				CloseLocationData closeLocationData = SerializationHelper.gson
						.fromJson(jsonCloseLocationData,
								CloseLocationData.class);
				return closeLocationData;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
