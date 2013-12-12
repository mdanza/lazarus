package com.android.lazarus.serviceadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.model.Favourite;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserServiceAdapterImpl implements UserServiceAdapter {
	Context context;

	public UserServiceAdapterImpl(Context context) {
		this.context = context;
	}

	@Override
	public String login(String username, String password) {
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(ConstantsHelper.REST_API_URL
				+ "/users/login");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String token = jsonResponse.get("data").getAsString();
				return token;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Favourite getFavourite(String token, String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
