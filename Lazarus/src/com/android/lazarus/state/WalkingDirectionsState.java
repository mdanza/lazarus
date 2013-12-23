package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapter;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapterImpl;

public class WalkingDirectionsState extends LocationDependentState {

	Point destination;
	List<WalkingPosition> positions;

	public WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context,30);
		this.destination = destination;
	}

	@Override
	protected void handleResults(List<String> results) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void giveAccurateInstructions() {
		GetInstructionsTask getInstructionsTask = new GetInstructionsTask();
		getInstructionsTask.doInBackground(new String[2]);
	}
	
	private class GetInstructionsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			DirectionsServiceAdapter directionsAdapter = new DirectionsServiceAdapterImpl();
			positions = directionsAdapter
					.getWalkingDirections(context.getToken(), position.toString(),
							Double.toString(destination.getLatitude())+","+Double.toString(destination.getLongitude()));

			message = "Ahora te debería decir que dobles a la derecha";
			tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
			return message;
			
		}

	}

}
