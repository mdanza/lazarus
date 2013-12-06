package com.android.lazarus.listener;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.state.DestinationSetState;
import com.android.lazarus.state.LocationDependentState;

public class LocationListenerImpl implements LocationListener {

	private LocationManager locationManager;
	private Location location = null;
	private VoiceInterpreterActivity voiceInterpreterActivity;

	public LocationListenerImpl(
			VoiceInterpreterActivity voiceInterpreterActivity) {
		this.voiceInterpreterActivity = voiceInterpreterActivity;
		locationManager = (LocationManager) voiceInterpreterActivity
				.getSystemService(Activity.LOCATION_SERVICE);

			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 10, this);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null)
				this.onLocationChanged(location);
	}

	@Override
	public void onLocationChanged(final Location location) {
		if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
			if (location == null) {
				((LocationDependentState) voiceInterpreterActivity.getState())
						.setGpsEnabled(false);
				((LocationDependentState) voiceInterpreterActivity.getState())
						.setPosition(location);
			} else {
				if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
					((LocationDependentState) voiceInterpreterActivity
							.getState()).setGpsEnabled(true);
					((LocationDependentState) voiceInterpreterActivity
							.getState()).setPosition(location);
				} else {
					((LocationDependentState) voiceInterpreterActivity
							.getState()).setGpsEnabled(false);
					((LocationDependentState) voiceInterpreterActivity
							.getState()).setPosition(null);
				}
			}

		}
		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
				if (LocationManager.GPS_PROVIDER.equals(provider)) {
					((LocationDependentState) voiceInterpreterActivity
							.getState()).setGpsEnabled(false);
					((LocationDependentState) voiceInterpreterActivity
							.getState()).setPosition(null);
				}
			}
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (LocationManager.GPS_PROVIDER.equals(provider)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 10, this);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
				((LocationDependentState) voiceInterpreterActivity.getState())
						.setGpsEnabled(true);
			}
			if (location != null)
				this.onLocationChanged(location);
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public Location getLocation() {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return null;
		} else {
			return location;
		}
	}

}
