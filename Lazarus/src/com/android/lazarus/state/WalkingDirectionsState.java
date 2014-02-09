package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
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
import com.vividsolutions.jts.geom.LineSegment;

public class WalkingDirectionsState extends LocationDependentState {
	private Point destination;
	private List<WalkingPosition> positions;
	private List<Obstacle> obstacles;
	private int currentWalkingPosition = 0;
	private String initialMessage = "";
	private double distanceToFinalPosition = -1;
	private static final int NEEDED_ACCURACY = 70;
	private BusRideState parentState;
	private InternalState state = InternalState.WAITING_TO_START;
	private Obstacle obstacleToReport = null;
	List<String> possibleDescriptions = null;
	ReportObstacleTask reportObstacleTask = new ReportObstacleTask();
	GetInstructionsTask getInstructionsTask = new GetInstructionsTask();
	String secondStreetInstruction = null;
	private String defaultMessage = "Párese sobre la vereda, y sostenga el celular frente a usted, paralelo al suelo, cuando esté listo, diga comenzar, ";
	private boolean defaultMessageSaid = false;
	private boolean hasSpoken = false;
	private boolean secondStreetInstructionGiven = false;
	private int walkingPositionProvider = WalkingPositionHelper.OSRM;
	private boolean hasSpokenAheadPosition = false;
	private int skipInstructionFrom = -1;
	private double lastDistanceToWalkingPosition = -1;
	private int b = -1; // Used to check if has to speak ahead instruction
	boolean firstWalkingInstructionGiven = false;
	private List<String> notGivenMessages = new ArrayList<String>();
	private String oldMessageForObstacle;

	private enum InternalState {
		WAITING_TO_START, WALKING_INSTRUCTIONS, SELECTING_OBSTACLE_DESCRIPTION, CONFIRMING_DESCRIPTION, WAITING_TO_RECALCULATE, RECALCULATE
	}

	public List<WalkingPosition> getPositions() {
		return positions;
	}

	public WalkingDirectionsState(VoiceInterpreterActivity context) {
		super(context);
		context.showToast(ConstantsHelper.OPEN_STREET_MAP_ACKNOWLEDGEMENT);
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
					context.setState(parentState, false);
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
		oldMessageForObstacle = message;
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
					if (!state.equals(InternalState.RECALCULATE)) {
						giveWalkingInstruction();
					}
				}
			}
		}
	}

	private void giveWalkingInstruction() {
		checkForNotGivenMessages();
		checkForObstacles();
		int olderPosition = currentWalkingPosition;
		int closestPosition = getClosestPosition();
		showDebugMessage(closestPosition);
		if (closestPosition != -1 && olderPosition + 1 == closestPosition) {
			currentWalkingPosition = getClosestPosition();
			hasSpoken = false;
		}
		if (conditionsToRecalculate()) {
			recalculate();
		} else {
			String instruction = null;
			if (hasToSpeakInstruction()) {
				instruction = getInstructionForCurrentWalkingPosition();
			}
			if (hasToSpeakAheadInstruction()) {
				if (instruction == null) {
					instruction = getAheadInstruction();
				} else {
					instruction += ". luego de hacerlo,, "
							+ getAheadInstruction();
				}
			}
			if (instruction != null) {
				if (firstWalkingInstructionGiven) {
					message = instruction;
					if (currentWalkingPosition == positions.size() - 1) {
						context.speak(instruction);
					} else {
						context.speak(instruction, true);
					}
					secondStreetInstructionGiven = true;
				} else {
					notGivenMessages.add(instruction);
				}
			}
		}
	}

	private void checkForNotGivenMessages() {
		if (firstWalkingInstructionGiven) {
			if (notGivenMessages != null && !notGivenMessages.isEmpty()) {
				for (int i = 0; i < notGivenMessages.size(); i++) {
					message = notGivenMessages.get(i);
					context.speak(message, true);
				}
				secondStreetInstructionGiven = true;
				notGivenMessages.clear();
			}
		}
	}

	private String getAheadInstruction() {
		String instruction = null;
		if (positions != null && currentWalkingPosition < positions.size() - 1) {
			int nextWithInstruction = WalkingPositionHelper
					.getNextPositionWithInstruction(currentWalkingPosition,
							positions);
			if (nextWithInstruction < positions.size() - 1) {
				instruction = WalkingPositionHelper
						.generateInstructionForNotFinalWalkingPosition(
								nextWithInstruction, positions);
				skipInstructionFrom = nextWithInstruction;
				hasSpokenAheadPosition = true;
			}
		}
		return instruction;
	}

	private boolean hasToSpeakAheadInstruction() {
		boolean hasToSpeak = false;
		if (WalkingPositionHelper.checkForValidPositionsForAheadInstruction(
				positions, currentWalkingPosition, position)) {
			if (!hasSpokenAheadPosition) {
				int c = WalkingPositionHelper.getNextPositionWithInstruction(
						currentWalkingPosition, positions); // Next position
															// with instruction
				Location p = position;
				if (c < positions.size() - 1) {
					if (d(currentWalkingPosition, c) < 80) {
						if (b == -1 || d(currentWalkingPosition, c) > 20) {
							b = currentWalkingPosition; // Walking position to
														// track
														// distance with next
														// walking
														// position with
														// instruction
						}
						int a = b - 1; // Previous walking position
						if (d(c, p) < 20) {
							hasToSpeak = true;
						}
						if (d(b, c) < 20) {
							if (b == 0 || d(a, p) > d(c, p)) {
								hasToSpeak = true;
							}
						}
						if (c == b + 1 || (c != b + 1 && d(b + 1, c) < 20)) {
							if (d(c, p) < (d(b, c) / 2) + 10) {
								hasToSpeak = true;
							}
						}
					}
				}
			}
		}
		return hasToSpeak;
	}

	private double d(int b, int c) {
		double distance = 100000;
		if (b < positions.size() && c < positions.size())
			distance = WalkingPositionHelper.distance(positions.get(b),
					positions.get(c));
		return distance;
	}

	private double d(int c, Location p) {
		double distance = 100000;
		if (p != null && c < positions.size())
			distance = WalkingPositionHelper.distanceToWalkingPosition(p,
					positions.get(c));
		return distance;
	}

	private boolean hasToSpeakInstruction() {
		boolean hasToSpeak = false;
		if (positions != null) {
			if (currentWalkingPosition > 0
					&& currentWalkingPosition != skipInstructionFrom) {
				if (currentWalkingPosition == positions.size() - 1
						&& position != null
						&& positions.get(currentWalkingPosition) != null) {
					WalkingPosition nowWalkingPosition = positions
							.get(currentWalkingPosition);
					if (!hasSpoken
							&& nowWalkingPosition != null
							&& nowWalkingPosition.getPoint() != null
							&& GPScoordinateHelper.getDistanceBetweenPoints(
									position.getLatitude(), nowWalkingPosition
											.getPoint().getLatitude(), position
											.getLongitude(), nowWalkingPosition
											.getPoint().getLongitude()) < 50) {
						hasToSpeak = true;
						hasSpoken = true;
					} else {
						if (hasSpoken)
							hasToSpeak = true;
					}
				}
				if (currentWalkingPosition < positions.size() - 1) {
					if (!hasSpoken
							&& position != null
							&& positions != null
							&& positions.get(currentWalkingPosition) != null
							&& positions.get(currentWalkingPosition).getPoint() != null
							&& positions.get(currentWalkingPosition)
									.getInstruction() != null) {
						Point point = positions.get(currentWalkingPosition)
								.getPoint();
						if (GPScoordinateHelper.getDistanceBetweenPoints(
								position.getLatitude(), point.getLatitude(),
								position.getLongitude(), point.getLongitude()) < (40 + position
								.getAccuracy())
								|| walkingPositionProvider == WalkingPositionHelper.MAP_QUEST) {
							hasToSpeak = true;
							hasSpoken = true;
						}
					}
				}
			} else {
				if (currentWalkingPosition == skipInstructionFrom) {
					skipInstructionFrom = -1;
					hasSpoken = true;
					hasSpokenAheadPosition = false;
					b = -1;
				}
			}
		}
		return hasToSpeak;
	}

	private void showDebugMessage(int closestPosition) {
		double newDistance = WalkingPositionHelper.distanceToWalkingPosition(
				position, positions.get(closestPosition));
		if (lastDistanceToWalkingPosition == -1
				|| Math.abs((newDistance - lastDistanceToWalkingPosition)) > 4) {
			lastDistanceToWalkingPosition = newDistance;
			String text = "Closest position: "
					+ closestPosition
					+ "\n"
					+ positions.get(closestPosition).getInstruction()
					+ "\n"
					+ "Now: "
					+ currentWalkingPosition
					+ "\n"
					+ positions.get(currentWalkingPosition).getInstruction()
					+ "Distance to current: "
					+ WalkingPositionHelper.distanceToWalkingPosition(position,
							positions.get(closestPosition)) + "\n"
					+ "Accuraccy: " + Math.ceil(position.getAccuracy());
			context.showToast(text);
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

	private int getClosestPosition() {
		int closestPosition = -1;
		if (positions != null && currentWalkingPosition < positions.size()
				&& position != null) {
			if (currentWalkingPosition < positions.size() - 1) {
				if (d(currentWalkingPosition, position) < d(
						currentWalkingPosition + 1, position)) {
					closestPosition = currentWalkingPosition;
				} else {
					closestPosition = currentWalkingPosition + 1;
				}
			} else {
				closestPosition = currentWalkingPosition;
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

				GeoPoint start = new GeoPoint(position.getLatitude(),
						position.getLongitude());
				GeoPoint end = new GeoPoint(destination.getLatitude(),
						destination.getLongitude());
				ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
				waypoints.add(start);
				waypoints.add(end);

				context.showToast(ConstantsHelper.OPEN_STREET_MAP_ACKNOWLEDGEMENT);
				RoadManager roadManager = new MapQuestRoadManager(
						ConstantsHelper.MAP_QUEST_API_KEY);
				roadManager.addRequestOption("routeType=pedestrian");
				roadManager.addRequestOption("locale=es_ES");
				if (isCancelled())
					return null;
				Road road = roadManager.getRoad(waypoints);
				ArrayList<GeoPoint> route = road.mRouteHigh;
				ArrayList<RoadNode> nodes = road.mNodes;
				positions = WalkingPositionHelper.createWalkingPositions(route,
						nodes, WalkingPositionHelper.MAP_QUEST);
				walkingPositionProvider = WalkingPositionHelper.MAP_QUEST;

				for (int i = 0; i < 3; i++) {
					if (!WalkingPositionHelper.isValidPositions(positions)) {
						roadManager = new MapQuestRoadManager(
								ConstantsHelper.MAP_QUEST_API_KEY);
						roadManager.addRequestOption("routeType=pedestrian");
						roadManager.addRequestOption("locale=es_ES");
						if (isCancelled())
							return null;
						context.sayMessage();
						if (isCancelled())
							return null;
						road = roadManager.getRoad(waypoints);
						route = road.mRouteHigh;
						nodes = road.mNodes;
						positions = WalkingPositionHelper
								.createWalkingPositions(route, nodes,
										WalkingPositionHelper.MAP_QUEST);
						walkingPositionProvider = WalkingPositionHelper.MAP_QUEST;
					}
				}
				// positions = null;

				for (int i = 0; i < 2; i++) {
					if (!WalkingPositionHelper.isValidPositions(positions)) {
						roadManager = new OSRMRoadManager();
						context.showToast(ConstantsHelper.OSRM_ACKNOWLEDGEMENT);
						if (isCancelled())
							return null;
						road = roadManager.getRoad(waypoints);
						route = road.mRouteHigh;
						nodes = road.mNodes;
						positions = WalkingPositionHelper
								.createWalkingPositions(route, nodes,
										WalkingPositionHelper.OSRM);
						walkingPositionProvider = WalkingPositionHelper.OSRM;
					}
				}
				
				if (isCancelled())
					return null;
				obstacles = obstacleReportingServiceAdapter
						.getObstaclesForRoute(route, context.getToken());

				if (WalkingPositionHelper.isValidPositions(positions)) {
					context.setWalkingWaypoints(positions);
					message = WalkingPositionHelper.translateFirstInstruction(
							positions.get(0).getInstruction(),
							positions.get(0), positions.get(1), context
									.getSensorEventListenerImpl().getAzimuth());
					if (state.equals(InternalState.RECALCULATE)) {
						boolean firstTurnMissed = false;
						if (secondStreetInstruction != null) {
							firstTurnMissed = WalkingPositionHelper
									.checkForFirstTurnMissed(
											secondStreetInstruction, positions)
									&& secondStreetInstructionGiven;
						}
						if (!firstTurnMissed) {
							restartAllState();
							return null;
						} else {
							message = WalkingPositionHelper
									.getFirstTurnMissedInstruction(secondStreetInstruction);
							state = InternalState.WAITING_TO_RECALCULATE;
							secondStreetInstruction = null;
							if (isCancelled())
								return null;
							context.speak(message, true);
							return null;
						}
					}
					if (!state.equals(InternalState.RECALCULATE)) {
						if (WalkingPositionHelper.distanceToWalkingPosition(
								position, positions.get(positions.size() - 1)) < 20
								&& WalkingPositionHelper
										.alwaysOnTheSameStreet(positions)) {
							currentWalkingPosition = positions.size() - 1;
							distanceToFinalPosition = WalkingPositionHelper
									.distanceToWalkingPosition(position,
											positions.get(positions.size() - 1));
							message = "Usted se encuentra aproximadamente a "
									+ Math.ceil(distanceToFinalPosition)
									+ " metros del destino, puede que tenga que cruzar la calle para llegar al mismo, al llegar diga destino";
						} else {
							message = initialMessage
									+ ", Para reportar un obstáculo en el camino, diga obstáculo, "
									+ ",, Ya no necesita sostener el celular frente a usted, "
									+ message;
							if (hasToSpeakAheadInstruction()) {
								message += ". luego,, " + getAheadInstruction();
							}
							if (isCancelled())
								return null;
							secondStreetInstruction = WalkingPositionHelper
									.getSecondStreetIntruction(positions);
						}
						if (isCancelled())
							return null;
						context.speak(message, true);
						firstWalkingInstructionGiven = true;
						return null;
					}
				} else {
					if (state.equals(InternalState.RECALCULATE)) {
						restartAllState();
						return null;
					} else {
						message = initialMessage
								+ "No se han podido obtener resultados para dirigirse a destino, ";
						if (isCancelled())
							return null;
						MainMenuState mainMenuState = new MainMenuState(
								context, message);
						context.setState(mainMenuState);
						return null;
					}
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
		WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(
				context, destination, parentState);
		context.setState(walkingDirectionsState);
	}

	@Override
	protected void cancel() {
		restartAllState();
	}

	@Override
	public void setPosition(Location position) {
		giveInstructions();
	}

	private class ReportObstacleTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... args) {
			message = "Registrando el obstáculo, por favor espere, ";
			ObstacleReportingServiceAdapter obstacleReportingServiceAdapter = new ObstacleReportingServiceAdapterImpl();
			boolean succeded = obstacleReportingServiceAdapter.reportObstacle(
					args[0], args[1], args[2], args[3]);
			if (succeded) {
				message = "Se ha registrado el obstáculo, la última instrucción dada fue,, "
						+ oldMessageForObstacle;
				state = InternalState.WALKING_INSTRUCTIONS;
			} else {
				message = "Ha ocurrido un problema al registrar el obstáculo, por favor diga la descripción del obstáculo que desea registrar, ";
				possibleDescriptions = null;
				state = InternalState.SELECTING_OBSTACLE_DESCRIPTION;
			}
			if (isCancelled())
				return null;
			context.sayMessage();
			return null;
		}
	}

	@Override
	public void onAttach() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void cancelAsyncTasks() {
		if (reportObstacleTask != null)
			reportObstacleTask.cancel(true);
		if (getInstructionsTask != null)
			getInstructionsTask.cancel(true);
	}

}