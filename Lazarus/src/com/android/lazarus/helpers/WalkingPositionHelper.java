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
	private static final String TURN_OPOSSITE_INTRUCTION = "Avanza en dirección opuesta por ";
	private static final String CONTINUE_INSTRUCCION = "Continúa en la misma dirección por ";
	private static final String TURN_LEFT_INSTRUCTION = "Avanza hacia la izquierda por ";
	private static final String TURN_RIGHT_INSTRUCTION = "Avanza hacia la derecha por ";

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

	public static String translateFirstInstruction(
			String firstMapQuestInstruction, Location currentLocation,
			WalkingPosition secondWalkingPosition, float azimuth) {
		String street = getStreetFromMapQuestFirstInstruction(firstMapQuestInstruction);
		String returnInstruction = "Avanza por ";
		if (azimuth != -1000) {
			Location secondLocation = createLocation(secondWalkingPosition);
			azimuth = (float) Math.toDegrees(azimuth);
			GeomagneticField geoField = new GeomagneticField(Double.valueOf(
					currentLocation.getLatitude()).floatValue(), Double
					.valueOf(currentLocation.getLongitude()).floatValue(),
					Double.valueOf(currentLocation.getAltitude()).floatValue(),
					System.currentTimeMillis());
			azimuth += geoField.getDeclination();
			if (azimuth < -180) {
				azimuth = 360 + azimuth;
			}

			// azimuth = azimuth + 180;
			float bearing = currentLocation.bearingTo(secondLocation);
			// bearing += 180;
			float direction = bearing - azimuth;
			boolean headingFound = false;
			if (Math.abs(direction)>140 && Math.abs(direction) < 180){
				returnInstruction = TURN_OPOSSITE_INTRUCTION;
				headingFound = true;
			}
			if(Math.abs(direction)>320 || Math.abs(direction)<40){
				returnInstruction = CONTINUE_INSTRUCCION;
				headingFound = true;
			}
			if (!headingFound && ((direction < 0 && Math.abs(direction) <180) || (direction > 0 && Math.abs(direction) > 180)))
				returnInstruction = TURN_LEFT_INSTRUCTION;
			if(!headingFound && ((direction > 0 && Math.abs(direction) >180) || (direction < 0 && Math.abs(direction) > 180)))
				returnInstruction = TURN_RIGHT_INSTRUCTION;
			returnInstruction += street + ", ";
			return returnInstruction;
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
					return TURN_RIGHT_INSTRUCTION;
				}
			}
		}
		return null;
	}

	private static String getStreetFromMapQuestFirstInstruction(
			String firstMapQuestInstruction) {
		String street = "la calle en que te encuentras";
		if (firstMapQuestInstruction != null) {
			boolean isStreetAfter = false;
			String[] splitted = firstMapQuestInstruction.split("\\ ");
			for (int i = 0; i < splitted.length; i++) {
				String word = splitted[i];
				if (!isStreetAfter) {
					if ("on".equals(word)) {
						isStreetAfter = true;
						street = "";
					}
				} else {
					if (i < splitted.length - 1) {
						street = word + " ";
					} else {
						street = street + word;
					}
				}
			}
		}
		return street;
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
