package com.android.lazarus.listener;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.state.LocationDependentState;

public class LocationListenerImpl implements LocationListener {

	private LocationManager locationManager;
	private Location location = null;
	private VoiceInterpreterActivity voiceInterpreterActivity;
	String provider = null;

	public LocationListenerImpl(){
		
	}
	
	public LocationListenerImpl(
			VoiceInterpreterActivity voiceInterpreterActivity) {
		this.voiceInterpreterActivity = voiceInterpreterActivity;
		locationManager = (LocationManager) voiceInterpreterActivity
				.getSystemService(Activity.LOCATION_SERVICE);
		requestUpdatesFromAllProviders();
		/*
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, true);
		if (bestProvider != null) {
			provider = bestProvider;
			locationManager.requestLocationUpdates(bestProvider, 1000, 1, this);
			Location location = locationManager
					.getLastKnownLocation(bestProvider);
			if (location != null)
				this.onLocationChanged(location);
			if (!bestProvider.equals(LocationManager.GPS_PROVIDER)) {
				requestUpdates(LocationManager.GPS_PROVIDER);
			}
		}else{
			requestUpdates(LocationManager.GPS_PROVIDER);
		}
		*/

	}

	private void requestUpdatesFromAllProviders() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 1, this);
	}

	/*
	private void requestUpdates(String updatesForProvider) {
			locationManager.requestLocationUpdates(updatesForProvider, 1000, 1,
					this);
	}
	*/

	@Override
	public void onLocationChanged(final Location location) {
		// Check if the provider has better accuracy
		if (location != null
				&& (this.location == null || location.getAccuracy() < this.location
						.getAccuracy())) {
			if (!location.getProvider().equals(provider)) {
				provider = location.getProvider();
			}
		}
		if (location != null && location.getProvider() != null
				&& (location.getProvider().equals(provider) || provider == null)) {
			if (voiceInterpreterActivity.getState() instanceof LocationDependentState) {
				((LocationDependentState) voiceInterpreterActivity.getState())
						.positionChanged(location);
			}
			this.location = location;
		}
		Context context = voiceInterpreterActivity.getApplicationContext();
		CharSequence text = "Accuracy = "+location.getAccuracy()+" Provided by: "+location.getProvider()+" actual provider: " +provider;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (provider!=null && provider.equals(this.provider)) {
			provider = null;
		}
		requestUpdatesFromAllProviders();
	}

	@Override
	public void onProviderEnabled(String newProvider) {
		requestUpdatesFromAllProviders();
		/*
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, true);
		provider = bestProvider;
		requestUpdates(bestProvider);
		if (LocationManager.GPS_PROVIDER.equals(newProvider)) {
			requestUpdates(newProvider);
		}
		*/
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		requestUpdatesFromAllProviders();
	}

	public Location getLocation() {
		return location;
	}

}