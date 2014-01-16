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
	private String[] points = { "-34.90111,-56.14013", "-34.901377,-56.14007",
			"-34.90162,-56.13995", "-34.90162,-56.13995",
			"-34.901736,-56.140301", "-34.901848,-56.140513",
			"-34.90171,-56.14033", "-34.901978,-56.140757",
			"-34.90171,-56.14033", "-34.902242,-56.141253",
			"-34.90254,-56.14187", "-34.90324,-56.14286",
			"-34.90432,-56.14469", "-34.90432,-56.14469",
			"-34.90490,-56.14446", "-34.90614,-56.14412",
			"-34.90614,-56.14412", "-34.90624,-56.14460" };

	public MockLocationListener(
			VoiceInterpreterActivity voiceInterpreterActivity) {
		this.voiceInterpreterActivity = voiceInterpreterActivity;
		Location location = new Location("");
		location.setLatitude(Double.valueOf(points[0].split("\\,")[0]));
		location.setLongitude(Double.valueOf(points[0].split("\\,")[1]));
		location.setAccuracy(20);
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
			location.setAccuracy(20);
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
				try {
					synchronized (locations) {
						locations.wait(3000);
					}
				} catch (InterruptedException e) {

				}
				if (locations.indexOf(location) != 0)
					onLocationChanged(location);
			}
			return "Walking";
		}
	}
}
