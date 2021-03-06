package com.android.lazarus.serviceadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.EncoderHelper;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserServiceAdapterImpl implements UserServiceAdapter {

	@Override
	public String login(String username, String password) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
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
	public boolean register(String username, String password, String email) {
		boolean result = false;
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpPost request = new HttpPost(ConstantsHelper.REST_API_URL + "/users");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK"))
				result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean usernameInUse(String username) {
		boolean result = false;
		HttpClient client = HttpClientCreator.getNewHttpClient();
		try {
			HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL
					+ "/users/username/"
					+ EncoderHelper.encode(username));
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String data = jsonResponse.get("data").getAsString();
				result = Boolean.valueOf(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean emailInUse(String email) {
		boolean result = false;
		HttpClient client = HttpClientCreator.getNewHttpClient();
		try {
			HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL
					+ "/users/email/"
					+ URLEncoder.encode(email, ConstantsHelper.ENCODING));
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				result = Boolean.getBoolean(jsonResponse.get("data")
						.getAsString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean deactivateUser(String token) {
		boolean result = false;
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL + "/users");
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonParser parser = new JsonParser();
			JsonObject jsonResponse = parser.parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String data = jsonResponse.get("data").getAsString();
				JsonObject jsonUser = parser.parse(data).getAsJsonObject();
				String username = jsonUser.get("username").getAsString();
				HttpDelete delRequest = new HttpDelete(
						ConstantsHelper.REST_API_URL + "/users?username="
								+ URLEncoder.encode(username, "UTF-8"));
				delRequest.addHeader("Authorization", token);
				response = client.execute(delRequest);
				rd = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				jsonResponse = parser.parse(rd.readLine()).getAsJsonObject();
				if (jsonResponse.get("result").getAsString().equals("OK"))
					result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean modifyPassword(String token, String newPassword) {
		boolean result = false;
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(ConstantsHelper.REST_API_URL + "/users");
		try {
			request.addHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonParser parser = new JsonParser();
			JsonObject jsonResponse = parser.parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				String data = jsonResponse.get("data").getAsString();
				JsonObject jsonUser = parser.parse(data).getAsJsonObject();
				String username = jsonUser.get("username").getAsString();
				String email = "";
				if (jsonUser.has("email"))
					email = jsonUser.get("email").getAsString();
				HttpPut putRequest = new HttpPut(ConstantsHelper.REST_API_URL
						+ "/users");
				putRequest.addHeader("Authorization", token);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair("username", username));
				nameValuePairs.add(new BasicNameValuePair("password",
						newPassword));
				nameValuePairs.add(new BasicNameValuePair("email", email));
				putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = client.execute(putRequest);
				rd = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				jsonResponse = parser.parse(rd.readLine()).getAsJsonObject();
				if (jsonResponse.get("result").getAsString().equals("OK"))
					result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
