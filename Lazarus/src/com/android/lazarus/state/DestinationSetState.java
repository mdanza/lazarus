package com.android.lazarus.state;

import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.listener.LocationListenerImpl;
import com.android.lazarus.model.Point;

public class DestinationSetState extends AbstractState {

	Point destination;
	LocationListenerImpl locationListener;
	
	
	public DestinationSetState(VoiceInterpreterActivity context, Point point) {
		super(context);
		this.destination = point;
		this.locationListener = context.getLocationListener();
		if(locationListener.getLocation()==null){
			this.message = "No se puede obtener su posición actual, por favor encienda el g p s";
		}else{
			this.message = "Usted se encuentra aproximadamente a x metros, si quiere ir en bus diga uno, si quiere ir a pie diga dos";
		}
	}

	@Override
	protected void handleResults(List<String> results) {
		if(this.containsNumber(results, 2)){
			WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(this.context,destination);
			this.context.setState(walkingDirectionsState);
		}

	}

}
