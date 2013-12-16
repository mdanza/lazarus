package com.android.lazarus.serviceadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.serviceadapter.utils.HttpClientCreator;
import com.android.lazarus.serviceadapter.utils.MySSLSocketFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserServiceAdapterImpl implements UserServiceAdapter {
	Context context;

	public UserServiceAdapterImpl(Context context) {
		this.context = context;
	}

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
	public boolean register(String username, String password, String email,
			String cellphone, String secretQuestion, String secretAnswer) {
		boolean result = false;
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(ConstantsHelper.REST_API_URL + "/users");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("cellphone", cellphone));
			nameValuePairs.add(new BasicNameValuePair("secretQuestion",
					secretQuestion));
			nameValuePairs.add(new BasicNameValuePair("secretAnswer",
					secretAnswer));
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

}
