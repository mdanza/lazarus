package com.android.lazarus.listener;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.state.LocationDependentState;

public class LocationListenerImpl implements LocationListener {

	private LocationManager locationManager;
	private Location location = null;
	private VoiceInterpreterActivity voiceInterpreterActivity;
	String provider = null;

	public LocationListenerImpl(
			VoiceInterpreterActivity voiceInterpreterActivity) {
		this.voiceInterpreterActivity = voiceInterpreterActivity;
		locationManager = (LocationManager) voiceInterpreterActivity
				.getSystemService(Activity.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		if (bestProvider != null) {
			provider = bestProvider;
			locationManager
					.requestLocationUpdates(bestProvider, 1000, 10, this);
			Location location = locationManager
					.getLastKnownLocation(bestProvider);
			if (location != null)
				this.onLocationChanged(location);
		}

	}

	@Override
	public void onLocationChanged(final Location location) {
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		if (bestProvider != null && !bestProvider.equals(provider)) {
			changeProvider(bestProvider);
		}
		if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
			((LocationDependentState) voiceInterpreterActivity.getState())
					.setPosition(location);
		}
		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
			if (LocationManager.GPS_PROVIDER.equals(provider)) {
				((LocationDependentState) voiceInterpreterActivity.getState())
						.setPosition(null);
			}
		}
	}

	@Override
	public void onProviderEnabled(String newProvider) {
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		changeProvider(bestProvider);
	}

	private void changeProvider(String bestProvider) {
		locationManager.removeUpdates(this);
		if (bestProvider != null) {
			locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
			Location location = locationManager
					.getLastKnownLocation(bestProvider);
			provider = bestProvider;
			if (location != null)
				this.onLocationChanged(location);
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public Location getLocation() {
		return location;
	}

}
