package com.android.lazarus.state;

import java.util.List;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.listener.LocationListenerImpl;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.DirectionsAdapter;
import com.android.lazarus.serviceadapter.DirectionsAdapterStub;

public class WalkingDirectionsState extends AbstractState {
	
	Point destination;
	LocationListenerImpl locationListener;
	Location myLocation;

	WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context);
		this.destination = destination;
		this.locationListener = context.getLocationListener();
		if(locationListener.getLocation()==null){
			this.message = "No se puede obtener su posición actual, por favor encienda el g p s";
		}else{
			myLocation = locationListener.getLocation();
			if(myLocation.getAccuracy()>20){
				this.message = "No se puede obtener su posición con exactitud, por favor si está en un luar cerrado salga";
			}else{
				giveWalkingDirections();
			}
		}
	}

	private void giveWalkingDirections() {
		DirectionsAdapter directionsAdapter = new DirectionsAdapterStub();
		List<WalkingPosition> positions = directionsAdapter.getWalkingDirections(myLocation,destination);
		this.message = "Ahora te debería decir que dobles a la derecha";
	}

	@Override
	protected void handleResults(List<String> results) {
		// TODO Auto-generated method stub

	}

}
