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
	private static final String TURN_OPOSSITE_INTRUCTION = "Avanza en la dirección opuesta a la que apunta el celular por ";
	private static final String CONTINUE_INSTRUCCION = "Avanza en la dirección a la que apunta el celular por ";
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
			if (Math.abs(direction) > 150 && Math.abs(direction) < 180) {
				returnInstruction = TURN_OPOSSITE_INTRUCTION;
				headingFound = true;
			}
			if (Math.abs(direction) > 330 || Math.abs(direction) < 30) {
				returnInstruction = CONTINUE_INSTRUCCION;
				headingFound = true;
			}
			if (!headingFound
					&& ((direction < 0 && Math.abs(direction) < 180) || (direction > 0 && Math
							.abs(direction) > 180)))
				returnInstruction = TURN_LEFT_INSTRUCTION;
			if (!headingFound
					&& ((direction > 0 && Math.abs(direction) < 180) || (direction < 0 && Math
							.abs(direction) > 180)))
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

	public static String generateInstructionForNotFinalWalkingPosition(
			int walkingPosition, List<WalkingPosition> positions) {
		String instruction = null;
		String currentInstruction = positions.get(walkingPosition)
				.getInstruction();
		if (currentInstruction != null) {
			boolean currentHasRight = hasRight(currentInstruction);
			boolean currentHasLeft = hasLeft(currentInstruction);
			boolean nextHasRight = false;
			boolean nextHasLeft = false;
			for (int i = walkingPosition + 1; i < positions.size(); i++) {
				if (!nextHasLeft && !nextHasRight) {
					String nextInstruction = positions.get(i).getInstruction();
					if (nextInstruction != null) {
						nextHasRight = hasRight(nextInstruction);
						nextHasLeft = hasLeft(nextInstruction);
					}
				}
			}
			if ((currentHasLeft && nextHasRight)
					|| (currentHasRight && nextHasLeft)) {
				instruction = "En la siguiente esquina, cruza la calle en la dirección en que estás avanzando, y luego "
						+ currentInstruction;
			} else {
				instruction = "En la siguiente esquina, " + currentInstruction;
			}
		}
		return instruction;
	}

	private static boolean hasRight(String instruction) {
		return hasString(instruction, "derecha")
				|| hasString(instruction, "Derecha")
				|| hasString(instruction, "DERECHA");
	}

	private static boolean hasLeft(String instruction) {
		return hasString(instruction, "izquierda")
				|| hasString(instruction, "Izquierda")
				|| hasString(instruction, "IZQUIERDA");
	}

	private static boolean hasString(String instruction, String string) {
		boolean hasString = false;
		if (instruction != null) {
			String[] splitInstruction = instruction.split("\\ ");
			for (String word : splitInstruction) {
				if (word != null && word.equals(string)) {
					hasString = true;
				}
			}
		}
		return hasString;
	}

	public static double distanceToWalkingPosition(Location position,
			WalkingPosition walkingPosition) {
		if (walkingPosition != null) {
			return GPScoordinateHelper.getDistanceBetweenPoints(walkingPosition
					.getPoint().getLatitude(), position.getLatitude(),
					walkingPosition.getPoint().getLongitude(), walkingPosition
							.getPoint().getLongitude());
		}
		return 0;
	}

	public static String getSecondStreetIntruction(
			List<WalkingPosition> positions) {
		String secondStreetInstruction = null;
		if (positions != null && positions.size() > 1) {
			String firstStreet = null;
			for (int i = 0; i < positions.size(); i++) {
				WalkingPosition position = positions.get(i);
				if (position != null) {
					String instruction = position.getInstruction();
					if (instruction != null) {
						if (firstStreet == null) {
							firstStreet = getFirstStreet(instruction
									.toLowerCase());
						} else {
							if (secondStreetInstruction == null) {
								secondStreetInstruction = getSecondStreetInstruction(
										instruction.toLowerCase(),
										firstStreet.toLowerCase());
							}
						}
					}
				}
			}
		}
		return secondStreetInstruction;
	}

	private static String getSecondStreetInstruction(String instruction,
			String firstStreet) {
		String secondStreetInstruction = null;
		if (instruction != null
				&& !instruction.toLowerCase().contains(
						firstStreet.toLowerCase())) {
			secondStreetInstruction = instruction.toLowerCase();
		}
		return secondStreetInstruction;
	}

	private static String getFirstStreet(String instruction) {
		instruction = instruction.toLowerCase();
		String firstStreet = null;
		if (instruction != null) {
			String[] words = instruction.split("\\ ");
			boolean passedOn = false;
			for (int i = 0; i < words.length; i++) {
				if (!passedOn && ("on".equals(words[i]))) {
					passedOn = true;
				}
				if (passedOn) {
					if (firstStreet == null) {
						firstStreet = words[i];
					} else {
						firstStreet = firstStreet + words[i];
					}
				}
			}
		}
		return firstStreet;
	}

	public static boolean checkForFirstTurnMissed(
			String oldSecondStreetInstruction, List<WalkingPosition> positions) {
		boolean firstTurnMissed = false;
		if (oldSecondStreetInstruction != null && positions != null) {
			String newSecondStreetInstruction = getSecondStreetIntruction(positions);
			if (newSecondStreetInstruction != null) {
				oldSecondStreetInstruction = oldSecondStreetInstruction
						.toLowerCase();
				newSecondStreetInstruction = newSecondStreetInstruction
						.toLowerCase();
				boolean hasRightOrLeft = false;
				if (oldSecondStreetInstruction.contains("derecha")) {
					hasRightOrLeft = true;
					newSecondStreetInstruction = newSecondStreetInstruction
							.replace("izquierda", "derecha");
				} else {
					if (oldSecondStreetInstruction.contains("izquierda")) {
						hasRightOrLeft = true;
						newSecondStreetInstruction = newSecondStreetInstruction
								.replace("derecha", "izquierda");
					}
				}
				if (hasRightOrLeft) {
					firstTurnMissed = newSecondStreetInstruction
							.equals(oldSecondStreetInstruction);
				}
			}
		}
		return firstTurnMissed;

	}

	public static String getFirstTurnMissedInstruction(
			String secondStreetInstruction) {
		String street = null;
		String turnDirection = null;
		if (secondStreetInstruction != null) {
			if (hasRight(secondStreetInstruction)) {
				turnDirection = "hacia la derecha";
			} else {
				if (hasLeft(secondStreetInstruction)) {
					turnDirection = "hacia la izquierda";
				}
			}
			street = getStreet(secondStreetInstruction);
		}
		if (street == null) {
			street = "la esquina en que debía";
		}
		if (turnDirection == null) {
			turnDirection = "";
		}
		String message = "No ha doblado "
				+ turnDirection
				+ " en "
				+ street
				+ ", es posible que la esquina se encuentre sólo en la vereda opuesta, si es así, "
				+ "por favor busque un cruce hacia la vereda opuesta,, una vez en la misma, puede reiniciar las instrucciones diciendo recalcular.. Si no es este el caso, diga recalcular ahora, ";
		return message;
	}

	private static String getStreet(String secondStreetInstruction) {
		String street = null;
		if (secondStreetInstruction != null) {
			String[] partsOfSecondStreet = secondStreetInstruction.split("\\ ");
			if (partsOfSecondStreet != null) {
				if (partsOfSecondStreet.length > 2) {
					String possibleIn = partsOfSecondStreet[partsOfSecondStreet.length - 2];
					if (!hasString(possibleIn, "en")
							|| !hasString(possibleIn, "por")) {
						street = partsOfSecondStreet[partsOfSecondStreet.length - 1];
					} else {
						street = partsOfSecondStreet[partsOfSecondStreet.length - 2]
								+ " "
								+ partsOfSecondStreet[partsOfSecondStreet.length - 1];
					}
				}
			}
		}
		return street;
	}

}
