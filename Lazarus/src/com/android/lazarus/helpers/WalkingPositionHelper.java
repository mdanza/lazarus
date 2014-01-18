package com.android.lazarus.helpers;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import android.hardware.GeomagneticField;
import android.location.Location;

import com.android.lazarus.model.Point;
import com.android.lazarus.model.WalkingPosition;

public class WalkingPositionHelper {

	private static final String[] MAPQUEST_ORIENTATIONS = { "sur", "suroeste",
			"oeste", "noroeste", "norte", "noreste", "este", "sureste", "sur" };
	private static final String[] SENSOR_ORIENTATIONS = { "S", "SW", "W", "NW",
			"N", "NE", "E", "SE", "S" };
	private static final String TURN_OPOSSITE_INTRUCTION = "Avanza en dirección opuesta por la calle en que te encuentras, ";
	private static final String CONTINUE_INSTRUCCION = "Continúa en la misma dirección por la calle en que te encuentras, ";
	private static final String TURN_LEFT_INSTRUCTION = "Avanza hacia la izquierda por la calle en que te encuentras, ";
	private static final String TURN_RIGTH_INSTRUCTION = "Avanza hacia la derecha por la calle en que te encuentras, ";

	public static List<WalkingPosition> createWalkingPositions(
			ArrayList<GeoPoint> route, ArrayList<RoadNode> nodes) {
		List<WalkingPosition> walkingPositions = new ArrayList<WalkingPosition>();
		if (route != null) {
			for (int i = 0; i < route.size(); i++) {
				GeoPoint point = route.get(i);
				WalkingPosition walkingPosition = new WalkingPosition();
				walkingPosition.setPoint(new Point(point.getLatitude(), point
						.getLongitude()));
				if (nodesContainPoint(nodes, point)) {
					walkingPosition.setInstruction(getInstructionForPoint(
							nodes, point));
				}
				walkingPositions.add(walkingPosition);
			}
		}
		return walkingPositions;

	}

	private static String getInstructionForPoint(ArrayList<RoadNode> nodes,
			GeoPoint point) {
		String instructions = null;
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				RoadNode node = nodes.get(i);
				if (node != null && node.mLocation != null
						&& node.mLocation.equals(point)) {
					instructions = node.mInstructions;
				}
			}
		}
		return instructions;
	}

	private static boolean nodesContainPoint(ArrayList<RoadNode> nodes,
			GeoPoint point) {
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				RoadNode node = nodes.get(i);
				if (node != null && node.mLocation != null
						&& node.mLocation.equals(point)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String translateFirstInstruction(Location currentLocation,
			WalkingPosition secondWalkingPosition, float azimuth, float roll) {
		String returnInstruction = "Continua por la calle en que te encuentras";
		if (azimuth != -1000) {
			Location secondLocation = createLocation(secondWalkingPosition);

			azimuth = (float) Math.toDegrees(azimuth);
			roll = (float) Math.toDegrees(roll);
			GeomagneticField geoField = new GeomagneticField(Double.valueOf(
					currentLocation.getLatitude()).floatValue(), Double
					.valueOf(currentLocation.getLongitude()).floatValue(),
					Double.valueOf(currentLocation.getAltitude()).floatValue(),
					System.currentTimeMillis());
			azimuth += geoField.getDeclination(); 
			roll += geoField.getDeclination(); // converts magnetic north
													// into
													// true north
			float bearing = currentLocation.bearingTo(secondLocation); // (it's
																		// already
																		// in
			// degrees)
			float direction = azimuth - bearing;
			float postDirection = roll - bearing;
			float nut = 0;
			float s = 2;
		}
		/*
		 * if (walkingPosition != null && walkingPosition.getInstruction() !=
		 * null) { String instruction = walkingPosition.getInstruction();
		 * String[] words = instruction.split("\\ "); String cardinalTarget =
		 * null; for (String cardinal : MAPQUEST_ORIENTATIONS) { if
		 * (stringPresent(words, cardinal)) { cardinalTarget = cardinal; } } if
		 * (cardinalTarget != null) { String cardinalDestination =
		 * translateCardinalDestination(cardinalTarget); returnInstruction =
		 * getInstruction(cardinalDestination, cardinalDevice); } }
		 */
		return returnInstruction;
	}

	private static Location createLocation(WalkingPosition walkingPosition) {
		Location location = new Location("Fake provider");
		location.setLatitude(walkingPosition.getPoint().getLatitude());
		location.setLongitude(walkingPosition.getPoint().getLongitude());
		return location;
	}

	private static String getInstruction(String cardinalDestination,
			String cardinalDevice) {
		String[] cardinals = rotateToCenter(cardinalDevice);
		for (int i = 0; i < cardinals.length; i++) {
			if (cardinals[i].equals(cardinalDestination)) {
				if (i == 0 || i == 8) {
					return TURN_OPOSSITE_INTRUCTION;
				}
				if (i == 4) {
					return CONTINUE_INSTRUCCION;
				}
				if (0 < i && i < 4) {
					return TURN_LEFT_INSTRUCTION;
				}
				if (8 > i && i > 4) {
					return TURN_RIGTH_INSTRUCTION;
				}
			}
		}
		return null;
	}

	private static String[] rotateToCenter(String cardinalDevice) {
		String[] rotated = new String[9];
		for (int i = 0; i < SENSOR_ORIENTATIONS.length; i++) {
			rotated[i] = SENSOR_ORIENTATIONS[i];
		}
		int maximumRotations = 30;
		int i = 0;
		while (i < maximumRotations && !rotated[4].equals(cardinalDevice)) {
			rotated = rotateRight(rotated);
		}
		return rotated;
	}

	private static String[] rotateRight(String[] toRotate) {
		String[] rotated = new String[toRotate.length];
		for (int i = 0; i < toRotate.length; i++) {
			if (i != toRotate.length - 1 && i != 0) {
				rotated[i] = toRotate[i - 1];
			}
			if (i == toRotate.length - 1) {
				rotated[i] = toRotate[i - 1];
				rotated[0] = rotated[i];
			}
		}
		return rotated;
	}

	private static String translateCardinalDestination(String cardinalTarget) {
		for (int i = 0; i < MAPQUEST_ORIENTATIONS.length; i++) {
			String cardinal = MAPQUEST_ORIENTATIONS[i];
			if (cardinal.equals(cardinalTarget)) {
				return SENSOR_ORIENTATIONS[i];
			}
		}
		return null;
	}

	private static boolean stringPresent(String[] results, String search) {
		boolean stringPresent = false;
		for (String result : results) {
			if (search.equals(result)) {
				stringPresent = true;
			}
		}
		return stringPresent;
	}

}
