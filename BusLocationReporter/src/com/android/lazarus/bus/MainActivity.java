package com.android.lazarus.bus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.lazarus.bus.deserializers.BusStopDeserializer;
import com.android.lazarus.bus.helpers.GPScoordinateHelper;
import com.android.lazarus.bus.httputils.HttpClientCreator;
import com.android.lazarus.bus.model.BusStop;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity implements LocationListener {
	// in meters
	private static final double MINIMUM_ACCEPTABLE_PRECISION = 100;
	public static final String REST_API_URL = "https://ec2-54-209-91-189.compute-1.amazonaws.com:8443/services-1.0-SNAPSHOT/v1/api";
	// public static final String REST_API_URL =
	// "https://10.0.2.2:8443/services-1.0-SNAPSHOT/v1/api";

	private static final String USER = "bus";
	private static final String PWD = "superBusesSiQueSi";
	private String token;
	private ScheduledFuture<?> helperTaskExecutor;
	private ScheduledFuture<?> loginTaskExecutor;
	private ScheduledFuture<?> locationSenderTaskExecutor;
	private ScheduledExecutorService scheduledExecutorService;

	private Button actionBtn;
	private EditText variantCodeField;
	private EditText subLineCodeField;
	private long lastPassedStopOrdinal;
	private boolean active;
	private LocationManager locationManager;
	private String provider;
	private double lastReportedlatitude;
	private double lastReportedlongitude;
	private boolean isLastReportSent;
	private List<BusStop> stops;

	private Gson gson = createGson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionBtn = (Button) findViewById(R.id.pushToStartStopBtn);
		variantCodeField = (EditText) findViewById(R.id.variantCodeField);
		subLineCodeField = (EditText) findViewById(R.id.subLineCodeField);
		active = false;
		isLastReportSent = false;
		lastPassedStopOrdinal = -1;
		scheduledExecutorService = Executors.newScheduledThreadPool(3);
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null)
			onLocationChanged(location);

		actionBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (active) {
					variantCodeField.setEnabled(true);
					subLineCodeField.setEnabled(true);
					actionBtn.setText("enviar datos de ubicación");
					stopSendingData();
				} else {
					LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
					boolean enabled = service
							.isProviderEnabled(LocationManager.GPS_PROVIDER);

					// check if enabled and if not send user to the GSP settings
					// Better solution would be to display a dialog and
					// suggesting to
					// go to the settings
					if (!enabled) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					} else {
						String inputVariantCode = variantCodeField.getText()
								.toString();
						String inputSubLineCode = subLineCodeField.getText()
								.toString();
						if (inputVariantCode.equals("")
								|| inputSubLineCode.equals(""))
							Toast.makeText(getApplicationContext(),
									"Códigos vacíos", Toast.LENGTH_SHORT)
									.show();
						else {
							variantCodeField.setEnabled(false);
							subLineCodeField.setEnabled(false);
							actionBtn.setText("parar envío de datos");
							sendData();
						}
					}
				}
				active = !active;
			}
		});
	}

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(BusStop.class, new BusStopDeserializer());
		return builder.create();
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/* Remove the location listener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	private class HttpHelper implements Runnable {

		@Override
		public void run() {
			loginTaskExecutor = scheduledExecutorService.scheduleAtFixedRate(
					new LoginTask(), 0, 30 * 60, TimeUnit.SECONDS);
			locationSenderTaskExecutor = scheduledExecutorService
					.scheduleAtFixedRate(new LocationSenderTask(), 10, 60,
							TimeUnit.SECONDS);
		}
	}

	private void sendData() {
		helperTaskExecutor = scheduledExecutorService.schedule(
				new HttpHelper(), 2, TimeUnit.SECONDS);
	}

	private void stopSendingData() {
		loginTaskExecutor.cancel(false);
		locationSenderTaskExecutor.cancel(false);
		helperTaskExecutor.cancel(false);
	}

	private class LoginTask implements Runnable {
		@Override
		public void run() {
			HttpClient client = HttpClientCreator.getNewHttpClient();
			HttpPost request = new HttpPost(REST_API_URL + "/users/login");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", USER));
				nameValuePairs.add(new BasicNameValuePair("password", PWD));
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = client.execute(request);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
						.getAsJsonObject();
				if (jsonResponse.get("result").getAsString().equals("OK"))
					token = jsonResponse.get("data").getAsString();
				else
					Toast.makeText(getApplicationContext(),
							"Error while attempting login", Toast.LENGTH_LONG)
							.show();
			} catch (Exception e) {
				Toast.makeText(
						getApplicationContext(),
						"Error connecting to server. Check your internet connectivity",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class LocationSenderTask implements Runnable {
		@Override
		public void run() {
			if (!isLastReportSent && token != null) {
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				long busId = settings.getLong("busId", -1);
				if (busId == -1)
					registerBus();
				else {
					if (stops == null) {
						busId = settings.getLong("busId", -1);
						loadBusStops(busId);
					}
					checkIfPassedStop();
					sendLocationData(busId);
				}
				isLastReportSent = true;
			}
		}
	}

	private void checkIfPassedStop() {
		if (stops != null)
			for (BusStop stop : stops) {
				if (GPScoordinateHelper.getDistanceBetweenPoints(
						lastReportedlatitude, stop.getLatitude(),
						lastReportedlongitude, stop.getLongitude()) <= 2 * MINIMUM_ACCEPTABLE_PRECISION) {
					lastPassedStopOrdinal = stop.getOrdinal();
					break;
				}
			}
	}

	private void sendLocationData(long busId) {
		String variantCode = variantCodeField.getText().toString();
		String subLineCode = subLineCodeField.getText().toString();
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpPut request = new HttpPut(REST_API_URL + "/bus/"
				+ String.valueOf(busId));
		try {
			request.setHeader("Authorization", token);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("variantCode",
					variantCode));
			nameValuePairs.add(new BasicNameValuePair("subLineCode",
					subLineCode));
			nameValuePairs.add(new BasicNameValuePair("latitude", String
					.valueOf(lastReportedlatitude)));
			nameValuePairs.add(new BasicNameValuePair("longitude", String
					.valueOf(lastReportedlongitude)));
			nameValuePairs.add(new BasicNameValuePair("lastPassedStopOrdinal",
					String.valueOf(lastPassedStopOrdinal)));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			client.execute(request);
		} catch (Exception e) {
			Toast.makeText(
					getApplicationContext(),
					"Error connecting to server. Check your internet connectivity",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void registerBus() {
		String variantCode = variantCodeField.getText().toString();
		String subLineCode = subLineCodeField.getText().toString();
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpPost request = new HttpPost(REST_API_URL + "/bus");
		try {
			request.setHeader("Authorization", token);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("variantCode",
					variantCode));
			nameValuePairs.add(new BasicNameValuePair("subLineCode",
					subLineCode));
			nameValuePairs.add(new BasicNameValuePair("latitude", String
					.valueOf(lastReportedlatitude)));
			nameValuePairs.add(new BasicNameValuePair("longitude", String
					.valueOf(lastReportedlongitude)));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				JsonObject bus = jsonResponse.get("data").getAsJsonObject();
				long id = bus.get("id").getAsLong();
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putLong("busId", id);
				editor.commit();
			} else
				Toast.makeText(getApplicationContext(),
						jsonResponse.get("data").getAsString(),
						Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(
					getApplicationContext(),
					"Error connecting to server. Check your internet connectivity",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void loadBusStops(long busId) {
		HttpClient client = HttpClientCreator.getNewHttpClient();
		HttpGet request = new HttpGet(REST_API_URL + "/bus/"
				+ String.valueOf(busId) + "/stops");
		try {
			request.setHeader("Authorization", token);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			JsonObject jsonResponse = new JsonParser().parse(rd.readLine())
					.getAsJsonObject();
			if (jsonResponse.get("result").getAsString().equals("OK")) {
				stops = gson.fromJson(jsonResponse.get("data"),
						new TypeToken<List<BusStop>>() {
						}.getType());
			} else
				Toast.makeText(getApplicationContext(),
						jsonResponse.get("data").getAsString(),
						Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(
					getApplicationContext(),
					"Error connecting to server. Check your internet connectivity",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		double accuracy = location.getAccuracy();
		if (accuracy <= MINIMUM_ACCEPTABLE_PRECISION) {
			lastReportedlatitude = location.getLatitude();
			lastReportedlongitude = location.getLongitude();
			isLastReportSent = false;
			if (active)
				Toast.makeText(
						this,
						"lat: " + lastReportedlatitude + "; lng: "
								+ lastReportedlongitude, Toast.LENGTH_SHORT)
						.show();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
