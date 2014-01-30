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
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;

public class WalkingDirectionsState extends LocationDependentState {
	private Point destination;
	private List<WalkingPosition> positions;
	private List<Obstacle> obstacles;
	private int currentWalkingPosition = 0;
	private String initialMessage = "";
	private double distanceToFinalPosition = -1;
	private static final int NEEDED_ACCURACY = 50;
	private BusRideState parentState;
	private InternalState state = InternalState.WAITING_TO_START;
	private Obstacle obstacleToReport = null;
	List<String> possibleDescriptions = null;
	ReportObstacleTask reportObstacleTask = new ReportObstacleTask();
	GetInstructionsTask getInstructionsTask = new GetInstructionsTask();
	String secondStreetInstruction = null;
	private String defaultMessage = "Párese sobre la vereda, y sostenga el celular frente a usted, paralelo al suelo, cuando esté listo, diga comenzar, ";
	private boolean defaultMessageSaid = false;

	private enum InternalState {
		WAITING_TO_START, WALKING_INSTRUCTIONS, SELECTING_OBSTACLE_DESCRIPTION, CONFIRMING_DESCRIPTION, WAITING_TO_RECALCULATE, RECALCULATE
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
		context.showToast(ConstantsHelper.OPEN_STREET_MAP_ACKNOWLEDGEMENT);
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		context.showToast(ConstantsHelper.OPEN_STREET_MAP_ACKNOWLEDGEMENT);
		giveInstructions();
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination, BusRideState parentState) {
		super(context, NEEDED_ACCURACY);
		this.parentState = parentState;
		this.destination = destination;
		context.showToast(ConstantsHelper.OPEN_STREET_MAP_ACKNOWLEDGEMENT);
		giveInstructions();
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context,
			Point destination, String initialMessage) {
		super(context, NEEDED_ACCURACY);
		this.initialMessage = initialMessage;
		this.destination = destination;
		context.showToast(ConstantsHelper.OPEN_STREET_MAP_ACKNOWLEDGEMENT);
		giveInstructions();
	}

	@Override
	protected void handleResults(List<String> results) {
		if (state.equals(InternalState.WAITING_TO_START)) {
			if (stringPresent(results, "comenzar")) {
				this.state = InternalState.WALKING_INSTRUCTIONS;
				giveInstructions();
			}
			return;
		}
		if (state.equals(InternalState.WAITING_TO_RECALCULATE)) {
			if (stringPresent(results, "recalcular")) {
				restartAllState();
			}
			return;
		}
		if (state.equals(InternalState.WALKING_INSTRUCTIONS)) {
			if (stringPresent(results, "destino")) {
				if (parentState == null) {
					MainMenuState mainMenuState = new MainMenuState(context);
					context.setState(mainMenuState);
				} else {
					context.setState(parentState);
					parentState.arrivedToDestination();
					return;
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
				message = "Espere mientras reportamos el obstáculo";
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
		if (reportObstacleTask.getStatus() != AsyncTask.Status.RUNNING) {
			if (reportObstacleTask.getStatus() == AsyncTask.Status.PENDING) {
				reportObstacleTask.execute(args);
			} else {
				if (reportObstacleTask.getStatus() == AsyncTask.Status.FINISHED) {
					reportObstacleTask = new ReportObstacleTask();
					reportObstacleTask.execute(args);
				}
			}
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
		if (state.equals(InternalState.WAITING_TO_START)) {
			message = defaultMessage;
			if (!defaultMessageSaid) {
				defaultMessageSaid = true;
				context.speak(defaultMessage, true);
			}
		}
		if (!state.equals(InternalState.WAITING_TO_START)
				&& !state.equals(InternalState.WAITING_TO_RECALCULATE)) {
			if (positions == null) {
				message = "Espere mientras se cargan las instrucciones para llegar a destino";
				if (getInstructionsTask.getStatus() != AsyncTask.Status.RUNNING) {
					if (getInstructionsTask.getStatus() == AsyncTask.Status.PENDING) {
						getInstructionsTask.execute();
					} else {
						if (getInstructionsTask.getStatus() == AsyncTask.Status.FINISHED) {
							getInstructionsTask = new GetInstructionsTask();
							getInstructionsTask.execute();
						}
					}
				} else {
					message = "Espere mientras se cargan las instrucciones para llegar a destino";
				}
			} else {
				if (position != null) {
					checkForObstacles();
					int olderPosition = currentWalkingPosition;
					int closestPosition = getClosestPosition();
					if (closestPosition != -1
							&& olderPosition + 1 == closestPosition) {
						currentWalkingPosition = getClosestPosition();
					}
					if (conditionsToRecalculate()) {
						recalculate();
					} else {
						if (olderPosition != currentWalkingPosition
								|| currentWalkingPosition == positions.size() - 1) {
							String instruction = getInstructionForCurrentWalkingPosition();
							if (instruction != null) {
								message = instruction;
								context.speak(instruction, true);
							}
						}
					}
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
		state = InternalState.RECALCULATE;
		restartStateToRecalculate("Usted se está alejando del camino pautado, espere mientras recalculamos su camino, ");
	}

	private boolean conditionsToRecalculate() {
		boolean conditionsToRecalculate = false;
		Coordinate standingOn = createJTSCoordinate(new Point(
				position.getLatitude(), position.getLongitude()));
		Coordinate[] points = getCoordinates(currentWalkingPosition);
		double distanceFromStandingOnToPath = -1;
		// First position
		if (points[0] == null && points[1] != null && points[2] != null) {
			distanceFromStandingOnToPath = distanceFromPointToLine(standingOn,
					points[1], points[2]);
		}
		// Middle position
		if (points[0] != null && points[1] != null && points[2] != null) {
			double firstDistanceFromStandingOnToPath = distanceFromPointToLine(
					standingOn, points[1], points[2]);
			double secondDistanceFromStandingOnToPath = distanceFromPointToLine(
					standingOn, points[0], points[1]);
			if (firstDistanceFromStandingOnToPath > secondDistanceFromStandingOnToPath) {
				distanceFromStandingOnToPath = secondDistanceFromStandingOnToPath;
			} else {
				distanceFromStandingOnToPath = firstDistanceFromStandingOnToPath;
			}
		}
		// Last position
		if (points[0] != null && points[1] != null && points[2] == null) {
			distanceFromStandingOnToPath = distanceFromPointToLine(standingOn,
					points[0], points[1]);
		}
		if (distanceFromStandingOnToPath > position.getAccuracy() + 40) {
			conditionsToRecalculate = true;
		}
		return conditionsToRecalculate;

	}

	private double distanceFromPointToLine(Coordinate standingOn,
			Coordinate coordinate, Coordinate coordinate2) {
		LineSegment lineSegment = new LineSegment(coordinate, coordinate2);
		Coordinate closestPointToStandingOn = lineSegment
				.closestPoint(standingOn);
		return GPScoordinateHelper.getDistanceBetweenPoints(standingOn.y,
				closestPointToStandingOn.y, standingOn.x,
				closestPointToStandingOn.x);
	}

	private Coordinate createJTSCoordinate(Point point) {
		if (point != null) {
			return new Coordinate(point.getLongitude(), point.getLatitude());
		} else {
			return null;
		}
	}

	private Coordinate[] getCoordinates(int currentWalkingPosition) {
		Coordinate[] points = new Coordinate[3];
		if (positions != null && positions.size() > 1) {
			WalkingPosition actualWalkingPosition = null;
			WalkingPosition previousWalkingPosition = null;
			WalkingPosition nextWalkingPosition = null;
			Point currentPoint = null;
			Point previousPoint = null;
			Point nextPoint = null;
			actualWalkingPosition = positions.get(currentWalkingPosition);
			if (currentWalkingPosition > 0)
				previousWalkingPosition = positions
						.get(currentWalkingPosition - 1);
			if (currentWalkingPosition < positions.size() - 1)
				nextWalkingPosition = positions.get(currentWalkingPosition + 1);
			if (actualWalkingPosition != null)
				currentPoint = actualWalkingPosition.getPoint();
			if (nextWalkingPosition != null)
				nextPoint = nextWalkingPosition.getPoint();
			if (previousWalkingPosition != null)
				previousPoint = previousWalkingPosition.getPoint();
			if (previousPoint != null) {
				points[0] = createJTSCoordinate(previousPoint);
			}
			if (currentPoint != null) {
				points[1] = createJTSCoordinate(currentPoint);
			}
			if (nextPoint != null) {
				points[2] = createJTSCoordinate(nextPoint);
			}
		}
		return points;

	}

	private com.vividsolutions.jts.geom.Point createJTSPoint(Point currentPoint) {
		if (currentPoint != null) {
			GeometryFactory geometryFactory = new GeometryFactory();
			Coordinate coordinate = new Coordinate(currentPoint.getLongitude(),
					currentPoint.getLatitude());
			return geometryFactory.createPoint(coordinate);
		} else {
			return null;
		}
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

	private class GetInstructionsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			message = "Espere mientras se cargan las instrucciones para llegar a destino";
			if (initialMessage == null) {
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
				// SImon bolivar
				// end = new GeoPoint(-34.904507,-56.159493);
				// start = new GeoPoint(-34.778024, -55.754501);
				// end = new GeoPoint(-34.771635, -55.749975);
				// end = new GeoPoint(-34.774473,-55.756437);
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
					if (state.equals(InternalState.RECALCULATE)) {
						boolean firstTurnMissed = false;
						if (secondStreetInstruction != null) {
							firstTurnMissed = WalkingPositionHelper
									.checkForFirstTurnMissed(
											secondStreetInstruction, positions);
						}
						if (!firstTurnMissed) {
							restartAllState();
							return null;
						} else {
							message = WalkingPositionHelper
									.getFirstTurnMissedInstruction(secondStreetInstruction);
							state = InternalState.WAITING_TO_RECALCULATE;
							secondStreetInstruction = null;
							context.speak(message, true);
							return null;
						}
					}
					if (!state.equals(InternalState.RECALCULATE)) {
						message = initialMessage
								+ message
								+ ". Ya no necesita sostener el celular frente a usted, ";
						context.speak(message, true);
						secondStreetInstruction = WalkingPositionHelper
								.getSecondStreetIntruction(positions);
						return null;
					}
				} else {
					message = initialMessage
							+ "No se han podido obtener resultados para dirigirse a destino";
					context.speak(message);
					MainMenuState mainMenuState = new MainMenuState(context);
					context.setState(mainMenuState);
					return null;
				}
			}
			return null;

		}

	}

	protected void restartStateToRecalculate(String initialMessage) {
		positions = null;
		obstacles = null;
		currentWalkingPosition = 0;
		this.initialMessage = initialMessage;
		distanceToFinalPosition = -1;
		obstacleToReport = null;
		possibleDescriptions = null;
		defaultMessageSaid = false;
		giveInstructions();
	}

	public void restartAllState() {
		positions = null;
		obstacles = null;
		currentWalkingPosition = 0;
		distanceToFinalPosition = -1;
		obstacleToReport = null;
		possibleDescriptions = null;
		secondStreetInstruction = null;
		defaultMessageSaid = false;
		state = InternalState.WAITING_TO_START;
		giveInstructions();
	}

	@Override
	protected void cancel() {
		restartAllState();
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
			message = "Registrando el obstáculo, por favor espere, ";
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