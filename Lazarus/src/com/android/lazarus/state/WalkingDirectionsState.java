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
import com.android.lazarus.helpers.GPScoordinateHelper;
import com.android.lazarus.helpers.WalkingPositionHelper;
import com.android.lazarus.model.Obstacle;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;
import com.android.lazarus.serviceadapter.ObstacleReportingServiceAdapter;
import com.android.lazarus.serviceadapter.ObstacleReportingServiceAdapterImpl;

public class WalkingDirectionsState extends LocationDependentState {

	private Point destination;
	private List<WalkingPosition> positions;
	private List<Obstacle> obstacles;
	private int currentWalkingPosition = 0;
	private double initialDistanceToCurrentPosition = -1;
	private double initialDistanceToNextPosition = -1;
	private String initialMessage = "";
	private double distanceToFinalPosition = -1;
	private static final int NEEDED_ACCURACY = 50;

	public WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		giveInstructions();
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination, String initialMessage) {
		super(context, NEEDED_ACCURACY);
		this.initialMessage = initialMessage;
		this.destination = destination;
		giveInstructions();
	}

	@Override
	protected void handleResults(List<String> results) {
		if (stringPresent(results, "listo")) {
			MainMenuState mainMenuState = new MainMenuState(context);
		}
	}

	@Override
	protected void giveInstructions() {
		if (initialMessage != null && !initialMessage.equals("")) {
			context.speak(initialMessage, true);
		}
		if (positions == null) {
			GetInstructionsTask getInstructionsTask = new GetInstructionsTask();
			getInstructionsTask.doInBackground(new String[2]);
		} else {
			if (position != null) {
				checkForObstacles();
				int olderPosition = currentWalkingPosition;
				int closestPosition = getClosestPosition();
				if (closestPosition != -1 && olderPosition < closestPosition) {
					currentWalkingPosition = getClosestPosition();
				}
				if (olderPosition != currentWalkingPosition
						&& currentWalkingPosition != positions.size() - 1) {
					String instruction = positions.get(currentWalkingPosition)
							.getInstruction();
					if (instruction != null) {
						context.speak("En el siguiente cruce, " + instruction,
								true);
					}
					initialDistanceToCurrentPosition = distanceToWalkingPosition(positions
							.get(currentWalkingPosition));
					if (currentWalkingPosition != positions.size() - 1) {
						initialDistanceToNextPosition = distanceToWalkingPosition(positions
								.get(currentWalkingPosition + 1));
					}
				} else if (currentWalkingPosition == positions.size() - 1) {
					double currentDistanceToFinalPosition = distanceToWalkingPosition(positions
							.get(currentWalkingPosition));
					if (distanceToFinalPosition == -1
							|| Math.abs(distanceToFinalPosition
									- currentDistanceToFinalPosition) > 5) {
						distanceToFinalPosition = currentDistanceToFinalPosition;
						context.speak(
								"Usted se encuentra aproximadamente a "
										+ Math.ceil(currentDistanceToFinalPosition)
										+ " metros del destino, puede que tenga que cruzar la calle para llegar al mismo, al llegar diga listo",
								true);
					}
				} else if (conditionsToRecalculate()) {
					recalculate();
				}
			}

		}

	}

	private void checkForObstacles() {
		if (position != null && obstacles != null && !obstacles.isEmpty()) {
			for (int i = 0; i < obstacles.size(); i++) {
				if (!obstacles.isEmpty()) {
					Obstacle obstacle = obstacles.get(i);
					double distanceToObstacle = GPScoordinateHelper
							.getDistanceBetweenPoints(obstacle.getCentre()
									.getLatitude(), position.getLatitude(),
									obstacle.getCentre().getLongitude(),
									position.getLongitude());
					if (distanceToObstacle <= obstacle.getRadius()) {
						context.speak(
								"Cuidado, proximamente se puede encontrar con un obstáculo con la siguiente descripción: "
										+ obstacle.getDescription() + ", ",
								true);
						obstacles.remove(obstacle);
					}
				}
			}
		}
	}

	private void recalculate() {
		restartState("Usted se está alejando del camino pautado, espere mientras recalculamos su camino, ");
	}

	private boolean conditionsToRecalculate() {
		boolean conditionsToRecalculate = false;
		if (positions != null) {
			if (currentWalkingPosition != positions.size() - 1) {
				WalkingPosition walkingPosition = positions
						.get(currentWalkingPosition);
				WalkingPosition nextWalkingPosition = positions
						.get(currentWalkingPosition + 1);
				if (walkingPosition != null && nextWalkingPosition != null) {
					double newDistanceToCurrentPosition = distanceToWalkingPosition(walkingPosition);
					double newDistanceToNextPosition = distanceToWalkingPosition(nextWalkingPosition);
					double differenceOfCurrent = newDistanceToCurrentPosition
							- initialDistanceToCurrentPosition;
					double differenceOfNext = newDistanceToNextPosition
							- initialDistanceToNextPosition;
					if (differenceOfCurrent > 50 && differenceOfNext > -20) {
						conditionsToRecalculate = true;
					}
				}
			}
		}
		return conditionsToRecalculate;

	}

	private double distanceToWalkingPosition(WalkingPosition walkingPosition) {
		if (walkingPosition != null) {
			return GPScoordinateHelper.getDistanceBetweenPoints(walkingPosition
					.getPoint().getLatitude(), position.getLatitude(),
					walkingPosition.getPoint().getLongitude(), walkingPosition
							.getPoint().getLongitude());
		}
		return 0;
	}

	private int getClosestPosition() {
		int closestPosition = -1;
		if (positions != null) {
			double distance = -1;
			for (int i = 0; i < positions.size(); i++) {
				WalkingPosition walkingPosition = positions.get(i);
				if (positions.get(i) != null) {
					double newDistance = distanceToWalkingPosition(walkingPosition);
					if (distance == -1 || newDistance < distance) {
						distance = newDistance;
						closestPosition = i;
					}
				}
			}
		}
		return closestPosition;
	}

	private class GetInstructionsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			if (position != null && destination != null) {
				ObstacleReportingServiceAdapter obstacleReportingServiceAdapter = new ObstacleReportingServiceAdapterImpl();

				RoadManager roadManager = new MapQuestRoadManager(
						ConstantsHelper.MAP_QUEST_API_KEY);
				roadManager.addRequestOption("routeType=pedestrian");
				roadManager.addRequestOption("locale=es_ES");
				GeoPoint start = new GeoPoint(position.getLatitude(),
						position.getLongitude());
				GeoPoint end = new GeoPoint(destination.getLatitude(),
						destination.getLongitude());
				// start = new GeoPoint(-34.778024, -55.754501);
				//end = new GeoPoint(-34.7785,-55.755885);
				ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
				waypoints.add(start);
				waypoints.add(end);
				Road road = roadManager.getRoad(waypoints);
				ArrayList<GeoPoint> route = road.mRouteHigh;
				ArrayList<RoadNode> nodes = road.mNodes;

				obstacles = obstacleReportingServiceAdapter
						.getObstaclesForRoute(route, context.getToken());
				positions = WalkingPositionHelper.createWalkingPositions(route,
						nodes);

				if (positions != null) {
					message = WalkingPositionHelper.translateFirstInstruction(
							positions.get(currentWalkingPosition), context
									.getSensorEventListenerImpl()
									.getPointingDirection());
					message = initialMessage + message;
					context.speak(message, true);
				} else {
					message = initialMessage
							+ "No se han podido obtener resultados para dirigirse a destino";
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
		WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(
				context, destination);
		context.setState(walkingDirectionsState);
	}

	protected void restartState(String initialMessage) {
		WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(
				context, destination, initialMessage);
		context.setState(walkingDirectionsState);
	}

}
