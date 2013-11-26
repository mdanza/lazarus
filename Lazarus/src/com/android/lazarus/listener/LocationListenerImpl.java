package com.android.lazarus.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationListenerImpl implements LocationListener {
	    
		private Location location = null;
		
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

}
