package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import android.location.Location;
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
	private BusRideState parentState;
	private InternalState state = InternalState.WALKING_INSTRUCTIONS;
	private Obstacle obstacleToReport = null;
	List<String> possibleDescriptions = null;
	ReportObstacleTask reportObstacleTask = new ReportObstacleTask();
	GetInstructionsTask getInstructionsTask = new GetInstructionsTask();

	private enum InternalState {
		WALKING_INSTRUCTIONS, SELECTING_OBSTACLE_DESCRIPTION, CONFIRMING_DESCRIPTION
	}

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
			Point destination, BusRideState parentState) {
		super(context, NEEDED_ACCURACY);
		this.parentState = parentState;
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
		if (state.equals(InternalState.WALKING_INSTRUCTIONS)) {
			if (stringPresent(results, "destino")) {
				if (parentState == null) {
					MainMenuState mainMenuState = new MainMenuState(context);
					context.setState(mainMenuState);
				} else {
					context.setState(parentState);
					parentState.arrivedToDestination();
				}
			}
			if (stringPresent(results, "obstaculo")) {
				initializeSelectingObstacleName();
			}
			if (!stringPresent(results, "destino")
					&& !stringPresent(results, "obstaculo"))
				context.speak("La última instrucción fue, " + message);
			return;

		}
		if (state.equals(InternalState.SELECTING_OBSTACLE_DESCRIPTION)) {
			if (possibleDescriptions == null) {
				possibleDescriptions = results;
			}
			message = "¿Desea que la descripción sea: " + results.get(0) + "?";
			state = InternalState.CONFIRMING_DESCRIPTION;
			return;

		}
		if (state.equals(InternalState.CONFIRMING_DESCRIPTION)) {
			if (stringPresent(results, "si")) {
				obstacleToReport.setDescription(possibleDescriptions.get(0));
				message = "";
				reportObstacle(obstacleToReport);
			}
			if (stringPresent(results, "no")) {
				possibleDescriptions.remove(0);
				if (!possibleDescriptions.isEmpty()) {
					message = "¿Desea que la descripción sea: "
							+ results.get(0) + "?";
				} else {
					message = "Repita nuevamente la descripción del obstáculo, ";
				}
			}
			return;

		}
	}

	private void reportObstacle(Obstacle obstacle) {
		String[] args = new String[4];
		args[0] = context.getToken();
		args[1] = obstacle.getCentre().getLatitude() + ","
				+ obstacle.getCentre().getLongitude();
		args[2] = Long.toString(obstacle.getRadius());
		args[3] = obstacle.getDescription();
		if (reportObstacleTask.getStatus() != AsyncTask.Status.PENDING){
			//TODO
			reportObstacleTask.execute(args);
		}
	}

	private void initializeSelectingObstacleName() {

		message = "Diga la descripción del obstáculo que desea registrar, ";
		obstacleToReport = new Obstacle();
		Point point = new Point(position.getLatitude(), position.getLongitude());
		obstacleToReport.setCentre(point);
		Double radius = Double.valueOf((Math.ceil(position.getAccuracy())));
		obstacleToReport.setRadius(radius.intValue());
		state = InternalState.SELECTING_OBSTACLE_DESCRIPTION;

	}

	@Override
	protected void giveInstructions() {
		if (initialMessage != null && !initialMessage.equals("")) {
			context.speak(initialMessage, true);
			initialMessage = null;
		}
		if (positions == null) {
			message = "";
			if (getInstructionsTask.getStatus() != AsyncTask.Status.RUNNING) {
				if (getInstructionsTask.getStatus() == AsyncTask.Status.PENDING){
					getInstructionsTask.execute(new String[2]);
				}else{
					if(getInstructionsTask.getStatus() == AsyncTask.Status.FINISHED){
						getInstructionsTask = new GetInstructionsTask();
						getInstructionsTask.execute(new String[2]);
					}
				}
			}
		} else {
			if (position != null) {
				checkForObstacles();
				int olderPosition = currentWalkingPosition;
				int closestPosition = getClosestPosition();
				if (closestPosition != -1 && olderPosition < closestPosition) {
					currentWalkingPosition = getClosestPosition();
				}
				if (olderPosition != currentWalkingPosition) {
					String instruction = getInstructionForCurrentWalkingPosition();
					if (instruction != null) {
						message = instruction;
						context.speak(instruction, true);
					}
				} else if (conditionsToRecalculate()) {
					recalculate();
				}
			}

		}

	}

	private String getInstructionForCurrentWalkingPosition() {
		String instruction = null;
		if (currentWalkingPosition == positions.size() - 1) {
			double currentDistanceToFinalPosition = WalkingPositionHelper
					.distanceToWalkingPosition(position,
							positions.get(currentWalkingPosition));
			if (distanceToFinalPosition == -1
					|| Math.abs(distanceToFinalPosition
							- currentDistanceToFinalPosition) > 5) {
				distanceToFinalPosition = currentDistanceToFinalPosition;
				instruction = "Usted se encuentra aproximadamente a "
						+ Math.ceil(currentDistanceToFinalPosition)
						+ " metros del destino, puede que tenga que cruzar la calle para llegar al mismo, al llegar diga destino";
			}
		} else {
			initialDistanceToCurrentPosition = WalkingPositionHelper
					.distanceToWalkingPosition(position,
							positions.get(currentWalkingPosition));
			initialDistanceToNextPosition = WalkingPositionHelper
					.distanceToWalkingPosition(position,
							positions.get(currentWalkingPosition + 1));
			if (positions.get(currentWalkingPosition).getInstruction() != null) {
				instruction = WalkingPositionHelper
						.generateInstructionForNotFinalWalkingPosition(
								currentWalkingPosition, positions);
			}
		}
		return instruction;
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
					if (distanceToObstacle <= obstacle.getRadius()
							+ position.getAccuracy()) {
						context.speak(
								"Cuidado, próximamente se puede encontrar con un obstáculo con la siguiente descripción: "
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
					double newDistanceToCurrentPosition = WalkingPositionHelper
							.distanceToWalkingPosition(position,
									walkingPosition);
					double newDistanceToNextPosition = WalkingPositionHelper
							.distanceToWalkingPosition(position,
									nextWalkingPosition);
					double differenceOfCurrent = newDistanceToCurrentPosition
							- initialDistanceToCurrentPosition;
					double differenceOfNext = newDistanceToNextPosition
							- initialDistanceToNextPosition;
					if (differenceOfCurrent > 35 && differenceOfNext > -10) {
						conditionsToRecalculate = true;
					}
				}
			}
		}
		return conditionsToRecalculate;

	}

	private int getClosestPosition() {
		int closestPosition = -1;
		if (positions != null) {
			double distance = -1;
			for (int i = 0; i < positions.size(); i++) {
				WalkingPosition walkingPosition = positions.get(i);
				if (positions.get(i) != null) {
					double newDistance = WalkingPositionHelper
							.distanceToWalkingPosition(position,
									walkingPosition);
					if (distance == -1 || newDistance < distance) {
						distance = newDistance;
						closestPosition = i;
					}
				}
			}
		}
		return closestPosition;
	}

	private class GetInstructionsTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... args) {
			if(initialMessage==null){
				initialMessage = "";
			}
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
				//end = new GeoPoint(-34.771635, -55.749975);
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

				if (positions != null && positions.size() > 1) {
					message = WalkingPositionHelper.translateFirstInstruction(
							positions.get(0).getInstruction(), position,
							positions.get(currentWalkingPosition + 1), context
									.getSensorEventListenerImpl().getAzimuth());
					message = initialMessage + message;
					context.speak(message, true);
					// context.mockLocationListener.startMoving();
				} else {
					message = initialMessage
							+ "No se han podido obtener resultados para dirigirse a destino";
					context.speak(message);
					MainMenuState mainMenuState = new MainMenuState(context);
					context.setState(mainMenuState);
				}
			}
			return null;

		}

	}

	protected void restartState(String initialMessage) {
		positions = null;
		obstacles = null;
		currentWalkingPosition = 0;
		initialDistanceToCurrentPosition = -1;
		initialDistanceToNextPosition = -1;
		this.initialMessage = initialMessage;
		distanceToFinalPosition = -1;
		state = InternalState.WALKING_INSTRUCTIONS;
		obstacleToReport = null;
		possibleDescriptions = null;
		giveInstructions();
		// int position = context.mockLocationListener.position;
		// context.mockLocationListener.restartFromPosition(position);
		// context.mockLocationListener.restart();
	}

	@Override
	protected void restartState() {
		restartState("");
	}

	@Override
	public void setPosition(Location position) {

		if (position == null) {
			this.message = notEnoughAccuracyMessage;
			context.speak(this.message);
		} else {
			if (!(position.getAccuracy() < minimumAccuraccy)) {
				enoughAccuraccy = false;
				this.message = notEnoughAccuracyMessage;
				context.speak(this.message);
			} else {
				enoughAccuraccy = true;
				this.position = position;
				giveInstructions();
			}
		}
	}

	private class ReportObstacleTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			ObstacleReportingServiceAdapter obstacleReportingServiceAdapter = new ObstacleReportingServiceAdapterImpl();
			boolean succeded = obstacleReportingServiceAdapter.reportObstacle(
					args[0], args[1], args[2], args[3]);
			if (succeded) {
				message = "Se ha registrado el obstáculo, continúe avanzando, ";
				state = InternalState.WALKING_INSTRUCTIONS;
			} else {
				message = "Ha ocurrido un problema al registrar el obstáculo, por favor diga la descripción del obstáculo que desea registrar, ";
				possibleDescriptions = null;
				state = InternalState.SELECTING_OBSTACLE_DESCRIPTION;
			}
			context.sayMessage();
			return message;
		}
	}

}