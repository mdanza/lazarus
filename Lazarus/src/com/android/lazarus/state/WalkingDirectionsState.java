package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapter;
import com.android.lazarus.serviceadapter.DirectionsServiceAdapterImpl;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class WalkingDirectionsState extends LocationDependentState {

	Point destination;
	List<WalkingPosition> positions;

	public WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context, 300);
		this.destination = destination;
		giveInstructions();
	}

	@Override
	protected void handleResults(List<String> results) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void giveInstructions() {
		if (positions == null) {
			GetInstructionsTask getInstructionsTask = new GetInstructionsTask();
			getInstructionsTask.doInBackground(new String[2]);
		} else {

		}
	}

	private class GetInstructionsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			if (position != null && destination != null) {
				DirectionsServiceAdapter directionsAdapter = new DirectionsServiceAdapterImpl();
				String origin = Double.toString(position.getLatitude()) + ","
						+ Double.toString(position.getLongitude());
				String end = Double.toString(destination.getLatitude()) + ","
						+ Double.toString(destination.getLongitude());
				end = "-34.768216,-55.761541";
				
				RoadManager roadManager = new MapQuestRoadManager(ConstantsHelper.MAP_QUEST_API_KEY);
				roadManager.addRequestOption("routeType=pedestrian");
				roadManager.addRequestOption("locale=es_ES");
				GeoPoint startPoint = new GeoPoint(-34.895928,-56.183251);
				ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
				waypoints.add(startPoint);
				waypoints.add(new GeoPoint(-34.915215,-56.151665));
				Road road = roadManager.getRoad(waypoints);

				if (positions != null) {
					MarkerOptions markerOptions = new MarkerOptions();
					for (WalkingPosition position : positions) {
						LatLng latLng = new LatLng(position.getPoint()
								.getLatitude(), position.getPoint()
								.getLongitude());
						markerOptions.position(latLng);
					}
					message = "Ahora te debería decir que dobles a la derecha";
					tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
				} else {
					message = "No se han podido obtener resultados para dirigirse a destino";
					context.speak(message);
					MainMenuState mainMenuState = new MainMenuState(context);
					context.setState(mainMenuState);
				}
			}
			return message;

		}

	}

	@Override
	protected void restartState() {
		WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(context, destination);
		context.setState(walkingDirectionsState);		
	}

}
