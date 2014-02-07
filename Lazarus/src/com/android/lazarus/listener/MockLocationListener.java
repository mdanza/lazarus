package com.android.lazarus.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.location.Location;
import android.os.Bundle;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.state.LocationDependentState;

public class MockLocationListener extends LocationListenerImpl {

	public int counter = 0;
	private Location location = null;
	private VoiceInterpreterActivity voiceInterpreterActivity;
	String provider = null;
	Boolean restarted = false;
	//MARCO BRUTO TO LUIS LAMAS
	 public String[] points = { "-34.90111,-56.14013", "-34.901377,-56.14007",
	 "-34.90162,-56.13995", "-34.90162,-56.13995",
	 "-34.901736,-56.140301", "-34.901848,-56.140513",
	 "-34.90171,-56.14033", "-34.901978,-56.140757",
	 "-34.90171,-56.14033", "-34.902242,-56.141253",
	 "-34.90254,-56.14187", "-34.90324,-56.14286",
	 "-34.90432,-56.14469", "-34.90432,-56.14469",
	 "-34.90490,-56.14446", "-34.90614,-56.14412",
	 "-34.90614,-56.14412", "-34.90624,-56.14460" };
	 
	 //RIVERA
	// public String[] points = { "-34.901641,-56.140131", "-34.901667,-56.140244" ,"-34.901687,-56.140322","-34.901714,-56.140475","-34.901747,-56.140612","-34.901793,-56.140778","-34.90183,-56.140934","-34.901854,-56.141041","-34.901938,-56.141025","-34.902033,-56.140995","-34.902121,-56.140958","-34.902244,-56.140907","-34.902473,-56.1408","-34.902695,-56.140716","-34.902862,-56.140663","-34.903055,-56.140585"};
	// public int position;

	//PONCE
	//public String[] points = { "-34.904217,-56.162515","-34.904265,-56.162445","-34.904294,-56.162464","-34.904312,-56.162488","-34.904347,-56.162512","-34.904389,-56.162539","-34.90443,-56.162563","-34.904468,-56.16259","-34.904498,-56.162619","-34.90454,-56.162646","-34.90458,-56.162662","-34.904604,-56.162678","-34.904626,-56.162708","-34.904655,-56.162732","-34.904716,-56.162764","-34.904795,-56.162831","-34.904888,-56.162901","-34.904982,-56.162963"};
	
	// alternative: para prueba bus D11
//	private String[] points = { "-34.90111,-56.14013", "-34.90111,-56.14013",
//			"-34.90111,-56.14013", "-34.90126,-56.140074",
//			"-34.901366,-56.140042", "-34.90163,-56.139935",
//			"-34.901568,-56.139634", "-34.901489,-56.139334",
//			"-34.901384,-56.139001", "-34.901252,-56.138583",
//			"-34.901181,-56.138314", "-34.899518,-56.133401",
//			"-34.897442,-56.124678", "-34.891563,-56.112286",
//			"-34.891053,-56.095045", "-34.893526,-56.092535",
//			"-34.890718,-56.071409", "-34.888906,-56.066431",
//			"-34.888105,-56.06391", "-34.887295,-56.061711",
//			"-34.886292,-56.058814", "-34.88623,-56.058578" };

	// alternative: para prueba bus D11 conexión L21
	// private String[] points = { "-34.90111,-56.14013", "-34.90111,-56.14013",
	// "-34.90111,-56.14013",
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

	public MockLocationListener(
			VoiceInterpreterActivity voiceInterpreterActivity, int position) {
		this.voiceInterpreterActivity = voiceInterpreterActivity;
		Location location = new Location("");
		location.setLatitude(Double.valueOf(points[position].split("\\,")[0]));
		location.setLongitude(Double.valueOf(points[position].split("\\,")[1]));
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
		// WalkTask walkTask = new WalkTask();
		// walkTask.execute(points);
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new WalkTask(),
				0, 3, TimeUnit.SECONDS);
	}

	@Override
	public void onLocationChanged(final Location location) {
		if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
			((LocationDependentState) voiceInterpreterActivity.getState())
					.positionChanged(location);
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

	private class WalkTask implements Runnable {
		public void run() {
			if (counter < points.length) {
				List<Location> locations = getLocationsFromPoints(points);
				location = locations.get(counter);
				onLocationChanged(location);
				counter++;
				voiceInterpreterActivity.showToast(location.getLatitude()
						+ ", " + location.getLongitude());
			}
		}
	}

	public void restart() {
		// restarted = true;
		// MockLocationListener mockLocationListener = new MockLocationListener(
		// this.voiceInterpreterActivity);
		// voiceInterpreterActivity.setLocationListener(mockLocationListener);
		counter = 0;
	}

	public void restartFromPosition(int position) {
		restarted = true;
//		MockLocationListener mockLocationListener = new MockLocationListener(
//				this.voiceInterpreterActivity, position);
//		voiceInterpreterActivity.setLocationListener(mockLocationListener);
//		mockLocationListener.position = position;
		counter = position;
	}
}
