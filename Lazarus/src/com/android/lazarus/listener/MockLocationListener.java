package com.android.lazarus.listener;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.state.LocationDependentState;

public class MockLocationListener extends LocationListenerImpl {

	private Location location = null;
	private VoiceInterpreterActivity voiceInterpreterActivity;
	String provider = null;
	Boolean restarted = false;
	// private String[] points = { "-34.90111,-56.14013",
	// "-34.901377,-56.14007",
	// "-34.90162,-56.13995", "-34.90162,-56.13995",
	// "-34.901736,-56.140301", "-34.901848,-56.140513",
	// "-34.90171,-56.14033", "-34.901978,-56.140757",
	// "-34.90171,-56.14033", "-34.902242,-56.141253",
	// "-34.90254,-56.14187", "-34.90324,-56.14286",
	// "-34.90432,-56.14469", "-34.90432,-56.14469",
	// "-34.90490,-56.14446", "-34.90614,-56.14412",
	// "-34.90614,-56.14412", "-34.90624,-56.14460" };

	// alternative: para prueba bus D11
	private String[] points = { "-34.90111,-56.14013", "-34.90126,-56.140074",
			"-34.901366,-56.140042", "-34.90163,-56.139935",
			"-34.901568,-56.139634", "-34.901489,-56.139334",
			"-34.901384,-56.139001", "-34.901252,-56.138583",
			"-34.901181,-56.138314", "-34.899518,-56.133401",
			"-34.897442,-56.124678", "-34.891563,-56.112286",
			"-34.891053,-56.095045", "-34.893526,-56.092535",
			"-34.890718,-56.071409", "-34.888906,-56.066431",
			"-34.888105,-56.06391", "-34.887295,-56.061711",
			"-34.886292,-56.058814", "-34.88623,-56.058578" };
	
	// alternative: para prueba bus D11 conexión L21
	// private String[] points = { "-34.90111,-56.14013",
	// "-34.90126,-56.140074",
	// "-34.901366,-56.140042", "-34.90163,-56.139935",
	// "-34.901568,-56.139634", "-34.901489,-56.139334",
	// "-34.901384,-56.139001", "-34.901252,-56.138583",
	// "-34.901181,-56.138314", "-34.899518,-56.133401",
	// "-34.897442,-56.124678", "-34.891563,-56.112286",
	// "-34.891053,-56.095045", "-34.893526,-56.092535",
	// "-34.890718,-56.071409", "-34.888906,-56.066431",
	// "-34.888105,-56.06391", "-34.887295,-56.061711",
	// "-34.886292,-56.058814", "-34.88623,-56.058578",
	// "-34.885773,-56.05743","-34.885288,-56.055788","-34.884831,-56.054533"
	// ,"-34.883942,-56.05464","-34.882921,-56.055198","-34.879858,-56.056722"
	// ,"-34.879251,-56.057011","-34.879515,-56.056915",
	// "-34.879832,-56.056775",
	// "-34.880157,-56.056614"};

	public MockLocationListener(
			VoiceInterpreterActivity voiceInterpreterActivity) {
		this.voiceInterpreterActivity = voiceInterpreterActivity;
		Location location = new Location("");
		location.setLatitude(Double.valueOf(points[0].split("\\,")[0]));
		location.setLongitude(Double.valueOf(points[0].split("\\,")[1]));
		location.setAccuracy(19);
		location.setAltitude(0);
		location.setTime(System.currentTimeMillis());
		location.setBearing(0F);
		onLocationChanged(location);
	}

	public void setPoints(String[] points) {
		this.points = points;
	}

	public List<Location> getLocationsFromPoints(String[] args) {
		List<Location> locations = new ArrayList<Location>();
		for (String point : args) {
			Location location = new Location("");
			location.setLatitude(Double.valueOf(point.split("\\,")[0]));
			location.setLongitude(Double.valueOf(point.split("\\,")[1]));
			location.setAccuracy(19);
			location.setAltitude(0);
			location.setTime(System.currentTimeMillis());
			location.setBearing(0F);
			locations.add(location);
		}
		return locations;
	}

	public void startMoving() {
		WalkTask walkTask = new WalkTask();
		walkTask.execute(points);
	}

	@Override
	public void onLocationChanged(final Location location) {
		if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
			((LocationDependentState) voiceInterpreterActivity.getState())
					.setPosition(location);
		}
		this.location = location;

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String newProvider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public Location getLocation() {
		return location;
	}

	private class WalkTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			List<Location> locations = getLocationsFromPoints(args);
			for (Location location : locations) {
				if (!restarted) {
					try {
						synchronized (locations) {
							locations.wait(3000);
						}
					} catch (InterruptedException e) {

					}
					if (locations.indexOf(location) != 0)
						onLocationChanged(location);
				}
			}
			return "Walking";
		}
	}

	public void restart() {
		restarted = true;
		MockLocationListener mockLocationListener = new MockLocationListener(
				this.voiceInterpreterActivity);
		voiceInterpreterActivity.setLocationListener(mockLocationListener);
	}
}
