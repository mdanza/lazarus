package com.android.lazarus.serviceadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.osmdroid.util.GeoPoint;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.SerializationHelper;
import com.android.lazarus.model.Favourite;
import com.android.lazarus.model.Obstacle;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ObstacleReportingServiceAdapterImpl implements
		ObstacleReportingServiceAdapter {

	@Override
	public boolean reportObstacle(String token, String coordinates,
			String radius, String description) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpPost request = new HttpPost(ConstantsHelper.REST_API_URL
				+ "/obstacles");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("coordinates",
					coordinates));
			nameValuePairs.add(new BasicNameValuePair("radius", radius));
			nameValuePairs.add(new BasicNameValuePair("description",
					description));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK"))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deactivateObstacle(String token, long id) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpDelete request = new HttpDelete(ConstantsHelper.REST_API_URL
				+ "/obstacles/" + id);
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK"))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Obstacle> getObstaclesForRoute(ArrayList<GeoPoint> route,
			String token) {
		HttpClient client = HttpClientCreator.getNewHttpClient();		
		try {
			HttpPost request = new HttpPost(ConstantsHelper.REST_API_URL
					+ "/obstacles/route");
			String stringRoute = SerializationHelper.gsonInvertedCoords.toJson(route);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("coordinates",
					stringRoute));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")){
				String jsonObstacles = jsonResponse.get("data").getAsString();
				Type type = new TypeToken<List<Obstacle>>() {
				}.getType();
				List<Obstacle> obstacles = SerializationHelper.gsonInvertedCoords.fromJson(
						jsonObstacles, type);
				return obstacles;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
