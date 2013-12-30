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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.SerializationHelper;
import com.android.lazarus.model.Favourite;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class FavouritesReportingServiceAdapterImpl implements
		FavouritesReportingServiceAdapter {

	@Override
	public boolean addToFavourite(String token, String coordinates, String name) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpPost request = new HttpPost(ConstantsHelper.REST_API_URL
				+ "/favourites");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("coordinates",
					coordinates));
			nameValuePairs.add(new BasicNameValuePair("name", name));
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
	public boolean removeFavourite(String token, String name) {
		HttpClient client = new DefaultHttpClient();
		HttpDelete request = new HttpDelete(ConstantsHelper.REST_API_URL
				+ "/favourites/" + name);
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
	public List<Favourite> getFavourites(String token) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL
				+ "/favourites");
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String jsonFavourites = jsonResponse.get("data").getAsString();
				Type type = new TypeToken<List<Favourite>>() {
				}.getType();
				List<Favourite> favourites = SerializationHelper.gson.fromJson(
						jsonFavourites, type);
				return favourites;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
