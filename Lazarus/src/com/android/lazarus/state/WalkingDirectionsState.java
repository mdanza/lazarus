package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.WalkingPositionHelper;
import com.android.lazarus.model.Obstacle;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.ObstacleReportingServiceAdapter;
import com.android.lazarus.serviceadapter.ObstacleReportingServiceAdapterImpl;

public class WalkingDirectionsState extends LocationDependentState {

	Point destination;
	List<WalkingPosition> positions;
	List<Obstacle> obstacles;
	int currentWalkingPosition;

	public WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context, 3000);
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
				ObstacleReportingServiceAdapter obstacleReportingServiceAdapter = new ObstacleReportingServiceAdapterImpl();
				
				RoadManager roadManager = new MapQuestRoadManager(ConstantsHelper.MAP_QUEST_API_KEY);
				roadManager.addRequestOption("routeType=pedestrian");
				roadManager.addRequestOption("locale=es_ES");
				GeoPoint start = new GeoPoint(position.getLatitude(),position.getLongitude());
				GeoPoint end = new GeoPoint(destination.getLatitude(),destination.getLongitude());
				start = new GeoPoint(-34.778024,-55.754501);
				end = new GeoPoint(-34.774305,-55.759072);
				ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
				waypoints.add(start);
				waypoints.add(end);
				Road road = roadManager.getRoad(waypoints);
				ArrayList<GeoPoint> route = road.mRouteHigh;
				ArrayList<RoadNode> nodes = road.mNodes;
				
				obstacles = obstacleReportingServiceAdapter.getObstaclesForRoute(route,context.getToken());
				positions = WalkingPositionHelper.createWalkingPositions(route,nodes);

				if (positions != null) {
					message = WalkingPositionHelper.translateFirstInstruction(positions.get(currentWalkingPosition),context.getSensorEventListenerImpl().getPointingDirection());
					context.speak(message);
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
