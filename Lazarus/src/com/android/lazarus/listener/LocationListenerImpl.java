package com.android.lazarus.listener;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.lazarus.VoiceInterpreterActivity;

public class LocationListenerImpl implements LocationListener {

	private LocationManager locationManager;
	private Location location = null;

	public LocationListenerImpl(
			VoiceInterpreterActivity voiceInterpreterActivity) {
		locationManager = (LocationManager) voiceInterpreterActivity
				.getSystemService(Activity.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1, 10, this);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null)
				this.onLocationChanged(location);
		}
	}

	@Override
	public void onLocationChanged(final Location location) {
		this.location = location;

	}

	@Override
	public void onProviderDisabled(String arg0) {
		location = null;

	}

	@Override
	public void onProviderEnabled(String provider) {
		if (LocationManager.GPS_PROVIDER.equals(provider)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1, 10, this);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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

	public void setLocation(Location location) {
		this.location = location;
	}

}
