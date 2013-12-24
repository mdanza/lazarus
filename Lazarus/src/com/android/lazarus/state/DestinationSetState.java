package com.android.lazarus.state;

import java.util.List;

import android.speech.tts.TextToSpeech;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.helpers.GPScoordinateHelper;
import com.android.lazarus.listener.LocationListenerImpl;
import com.android.lazarus.model.Point;

public class DestinationSetState extends LocationDependentState {

	Point destination;
	boolean firstIntructionPassed;

	public DestinationSetState(VoiceInterpreterActivity context,
			Point destination) {
		super(context, 200);
		this.destination = destination;
		giveInstructions();
	}
	
	public DestinationSetState(VoiceInterpreterActivity context){
		super(context);
	}

	@Override
	protected void handleResults(List<String> results) {
		if (this.containsNumber(results, 2)) {
			WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(
					this.context, destination);
			this.context.setState(walkingDirectionsState);
		}
	}

	@Override
	protected void giveInstructions() {
		if (!firstIntructionPassed && destination!=null && position!=null) {
			firstIntructionPassed=true;
			Double approximateDistance = GPScoordinateHelper.getDistanceBetweenPoints(this.position.getLatitude(), destination.getLatitude(), this.position.getLongitude(), destination.getLongitude());
			approximateDistance = approximateDistance/1000;
			approximateDistance = Math.floor(approximateDistance * 10) / 10;
			this.message = "Usted se encuentra aproximadamente a "+approximateDistance+" kilómetros del destino, si quiere ir en bus diga uno, si quiere ir a pie diga dos";
			tts.speak(this.message, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

}
