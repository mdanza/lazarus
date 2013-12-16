package com.android.lazarus.state;

import java.util.List;

import android.speech.tts.TextToSpeech;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapter;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapterImpl;

public class WalkingDirectionsState extends LocationDependentState {

	Point destination;

	WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context,30);
		this.destination = destination;
		giveInstructions();

	}

	@Override
	protected void handleResults(List<String> results) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void giveInstructions() {
		if(enoughAccuraccy){
			DirectionsServiceAdapter directionsAdapter = new DirectionsServiceAdapterImpl();
			//List<WalkingPosition> positions = directionsAdapter
				//	.getWalkingDirections(context.getToken(), this.position.toString(),
					//		destination.latitude);
			this.message = "Ahora te debería decir que dobles a la derecha";
			tts.speak(this.message, TextToSpeech.QUEUE_FLUSH, null);
			
		}
		
	}

}
