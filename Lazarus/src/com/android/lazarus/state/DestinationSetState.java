package com.android.lazarus.state;

import java.util.List;

import android.speech.tts.TextToSpeech;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.listener.LocationListenerImpl;
import com.android.lazarus.model.Point;

public class DestinationSetState extends LocationDependentState {

	Point destination;
	
	
	public DestinationSetState(VoiceInterpreterActivity context, Point destination) {
		super(context,20);
		this.destination = destination;
		if(this.gpsEnabled==true && this.enoughAccuraccy==true){
			giveInstructions();
		}
	}

	@Override
	protected void handleResults(List<String> results) {
		if(this.containsNumber(results, 2)){
			WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(this.context,destination);
			this.context.setState(walkingDirectionsState);
		}
	}

	@Override
	protected void giveInstructions() {
		this.message = "Usted se encuentra aproximadamente a x metros, si quiere ir en bus diga uno, si quiere ir a pie diga dos";
		tts.speak(this.message, TextToSpeech.QUEUE_FLUSH, null);
	}

}
