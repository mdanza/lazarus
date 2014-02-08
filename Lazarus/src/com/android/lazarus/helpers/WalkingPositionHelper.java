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

	private static final String TURN_OPOSSITE_INTRUCTION = "Avanza en la dirección opuesta a la que apunta el celular por ";
	private static final String CONTINUE_INSTRUCCION = "Avanza en la dirección a la que apunta el celular por ";
	private static final String TURN_LEFT_INSTRUCTION = "Avanza hacia la izquierda por ";
	private static final String TURN_RIGHT_INSTRUCTION = "Avanza hacia la derecha por ";
	public static final int MAP_QUEST = 0;
	public static final int OSRM = 1;
	private static final String[] OSRM_INSTRUCTIONS = { "Unknown instruction",
			"Continue", "Turn slight right", "Turn right", "Turn sharp right",
			"U-Turn", "Turn sharp left", "Turn left", "Turn slight left",
			"You have reached a waypoint of your trip",
			"Enter roundabout and leave at first exit",
			"Enter roundabout and leave at second exit",
			"Enter roundabout and leave at third exit",
			"Enter roundabout and leave at fourth exit",
			"Enter roundabout and leave at fifth exit",
			"Enter roundabout and leave at sixth exit",
			"Enter roundabout and leave at seventh exit",
			"Enter roundabout and leave at eighth exit",
			"Enter roundabout and leave at nineth exit",
			"You have reached your destination" };
	private static final String[] TRANSLATED_INSTRUCTIONS = { "Avanza",
			"Continúa avanzando por", "Gira ligeramente a la derecha en",
			"Gira a la derecha en", "Gire totalmente a la derecha en",
			"Haz un giro en U en", "Gira totalmente a la izquierda en",
			"Gira a la izquierda en", "Gira ligeramente a la izquierda en",
			"Continúa avanzando", "Gira en", "Gira en", "Gira en", "Gira en",
			"Gira en", "Gira en", "Gira en", "Gira en", "Gira en", "Destino" };
	private static final int OPENING_ANGLE = 45;

	public static List<WalkingPosition> createWalkingPositions(
			ArrayList<GeoPoint> route, ArrayList<RoadNode> nodes, int provider) {
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
			if (provider == OSRM)
				walkingPositions = translateWalklingPositions(walkingPositions);
		}
		return walkingPositions;

	}

	private static List<WalkingPosition> translateWalklingPositions(
			List<WalkingPosition> walkingPositions) {
		ArrayList<WalkingPosition> newWalkingPositions = null;
		if (walkingPositions != null) {
			newWalkingPositions = new ArrayList<WalkingPosition>();
			for (int i = 0; i < walkingPositions.size(); i++) {
				if (walkingPositions.get(i) != null) {
					WalkingPosition walkingPosition = walkingPositions.get(i);
					if (walkingPosition.getInstruction() != null) {
						walkingPosition = translateInstruction(walkingPosition);
					}
					newWalkingPositions.add(walkingPosition);
				}
			}
		}
		return newWalkingPositions;
	}

	private static WalkingPosition translateInstruction(
			WalkingPosition walkingPosition) {
		WalkingPosition newWalkingPosition = null;
		if (walkingPosition != null) {
			newWalkingPosition = new WalkingPosition();
			newWalkingPosition.setPoint(walkingPosition.getPoint());
			newWalkingPosition
					.setInstruction(translateInstruction(walkingPosition
							.getInstruction()));
		}
		return newWalkingPosition;
	}

	private static String translateInstruction(String instruction) {
		String newInstruction = null;
		if (instruction != null) {
			newInstruction = instruction;
			for (int i = 0; i < OSRM_INSTRUCTIONS.length; i++) {
				String oSRMInstruction = OSRM_INSTRUCTIONS[i];
				if (newInstruction.contains(oSRMInstruction)) {
					newInstruction = newInstruction.replace(" on ", " ");
					newInstruction = newInstruction.replace(oSRMInstruction,
							TRANSLATED_INSTRUCTIONS[i]);
					return newInstruction;
				}
			}
		}
		return newInstruction;
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
			String firstMapQuestInstruction,
			WalkingPosition firstWalkingPosition,
			WalkingPosition secondWalkingPosition, float azimuth) {

		if (azimuth != -1000 && firstWalkingPosition != null
				&& firstWalkingPosition.getPoint() != null
				&& secondWalkingPosition != null
				&& secondWalkingPosition.getPoint() != null) {
			String returnInstruction = "Avanza por ";
			String street = getStreetFromMapQuestFirstInstruction(firstMapQuestInstruction);
			Location secondLocation = createLocation(secondWalkingPosition);
			Location currentLocation = createLocation(firstWalkingPosition);
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
			if (Math.abs(direction) > 180 - OPENING_ANGLE
					&& Math.abs(direction) < 180 + OPENING_ANGLE) {
				returnInstruction = TURN_OPOSSITE_INTRUCTION;
				headingFound = true;
			}
			if (Math.abs(direction) > 360 - OPENING_ANGLE
					|| Math.abs(direction) < OPENING_ANGLE) {
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
		} else {
			return "Avanza por la calle en que te encuentras, ";
		}
	}

	private static Location createLocation(WalkingPosition walkingPosition) {
		Location location = new Location("Fake provider");
		location.setLatitude(walkingPosition.getPoint().getLatitude());
		location.setLongitude(walkingPosition.getPoint().getLongitude());
		return location;
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
					// if (i < splitted.length - 1) {
					// street = word + " ";
					// } else {
					street = street + " " + word;
					// }
				}
			}
		}
		return street;
	}

	public static String generateInstructionForNotFinalWalkingPosition(
			int walkingPosition, List<WalkingPosition> positions) {
		String instruction = null;
		String currentInstruction = positions.get(walkingPosition)
				.getInstruction();
		if (currentInstruction != null
				&& !streetRepeated(walkingPosition, positions)) {
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

	private static boolean streetRepeated(int walkingPosition,
			List<WalkingPosition> positions) {
		boolean streetRepeated = false;
		if (positions != null && walkingPosition < positions.size()) {
			String lastStreet = getLastStreet(positions, walkingPosition);
			String thisStreet = getStreet(positions.get(walkingPosition)
					.getInstruction());
			if (lastStreet != null && thisStreet != null
					&& lastStreet.equals(thisStreet)) {
				streetRepeated = true;
			}
		}
		return streetRepeated;
	}

	private static String getLastStreet(List<WalkingPosition> positions,
			int walkingPosition) {
		String lastStreet = null;
		if (positions != null && positions.size() > walkingPosition) {
			for (int i = 0; i < walkingPosition; i++) {
				if (lastStreet == null) {
					if (i == 0) {
						lastStreet = getFirstStreet(positions.get(i)
								.getInstruction());
					} else {
						lastStreet = getStreet(positions.get(i)
								.getInstruction());
					}
				} else {
					if (getStreet(positions.get(i).getInstruction()) != null) {
						lastStreet = getStreet(positions.get(i)
								.getInstruction());
					}
				}

			}
		}
		return lastStreet;
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
		if (walkingPosition != null && position != null) {
			return GPScoordinateHelper.getDistanceBetweenPoints(walkingPosition
					.getPoint().getLatitude(), position.getLatitude(),
					walkingPosition.getPoint().getLongitude(), position
							.getLongitude());
		}
		return 10000;
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
							firstStreet = getFirstStreet(instruction);
						} else {
							if (secondStreetInstruction == null) {
								secondStreetInstruction = getSecondStreetInstruction(
										instruction, firstStreet);
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
		if (instruction != null && !instruction.contains(firstStreet)) {
			secondStreetInstruction = instruction;
		}
		return secondStreetInstruction;
	}

	private static String getFirstStreet(String instruction) {
		String firstStreet = null;
		if (instruction != null) {
			String[] words = instruction.split("\\ ");
			boolean passedOn = false;
			for (int i = 0; i < words.length; i++) {
				if (passedOn) {
					if (firstStreet == null) {
						firstStreet = words[i];
					} else {
						firstStreet = firstStreet + " " + words[i];
					}
				}
				if (!passedOn && ("on".equals(words[i]))) {
					passedOn = true;
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
				boolean hasRightOrLeft = false;
				if (hasRight(oldSecondStreetInstruction)) {
					hasRightOrLeft = true;
					newSecondStreetInstruction = newSecondStreetInstruction
							.replace("izquierda", "derecha");
					newSecondStreetInstruction = newSecondStreetInstruction
							.replace("Izquierda", "Derecha");
					newSecondStreetInstruction = newSecondStreetInstruction
							.replace("IZQUIERDA", "DERECHA");
				} else {
					if (hasLeft(oldSecondStreetInstruction)) {
						hasRightOrLeft = true;
						newSecondStreetInstruction = newSecondStreetInstruction
								.replace("derecha", "izquierda");
						newSecondStreetInstruction = newSecondStreetInstruction
								.replace("Derecha", "Izquierda");
						newSecondStreetInstruction = newSecondStreetInstruction
								.replace("DERECHA", "IZQUIERDA");
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
				+ ",, es posible que la esquina se encuentre sólo en la vereda opuesta,, si es así,, "
				+ "busque un cruce hacia la vereda opuesta,, una vez en la misma,, puede reiniciar las instrucciones diciendo recalcular.. Si no es este el caso, diga recalcular ahora, ";
		return message;
	}

	private static String getStreet(String streetInstruction) {
		String street = null;
		if (streetInstruction != null) {
			String[] partsOfSecondStreet = streetInstruction.split("\\ ");
			if (partsOfSecondStreet != null) {
				for (int i = 0; i < partsOfSecondStreet.length; i++) {
					boolean afterIn = false;
					String possibleIn = partsOfSecondStreet[i];
					if (!afterIn
							&& (hasString(possibleIn, "en") || hasString(
									possibleIn, "por"))) {
						afterIn = true;
						street = "";
					} else {
						if (!"".equals(street)) {
							street = street + " " + partsOfSecondStreet[i];
						} else {
							street += partsOfSecondStreet[i];
						}
					}
				}
			}
		}
		return street;
	}

	public static boolean isValidPositions(List<WalkingPosition> positions) {
		boolean validPositions = false;
		if (positions != null && positions.size() > 1) {
			validPositions = true;
			if (positions.size() == 2) {
				if (positions.get(0) != null && positions.get(1) != null) {
					if (positions.get(0).getInstruction() == null
							&& positions.get(1).getInstruction() == null) {
						validPositions = false;
					}
				} else {
					validPositions = false;
				}
			}
		}
		return validPositions;
	}

	public static boolean alwaysOnTheSameStreet(List<WalkingPosition> positions) {
		boolean alwaysOnTheSameStreet = false;
		if (positions != null) {
			alwaysOnTheSameStreet = true;
			String street = null;
			for (int i = 0; i < positions.size(); i++) {
				if (positions.get(i) != null
						&& positions.get(i).getInstruction() != null) {
					String instruction = positions.get(i).getInstruction();
					if (i == 0) {
						street = getFirstStreet(instruction);
					} else {
						if (street == null) {
							street = getStreet(instruction);
						} else {
							if (!street.equals(getStreet(instruction))
									&& i != positions.size() - 1) {
								alwaysOnTheSameStreet = false;
							}
						}
					}
				}
			}
		}
		return alwaysOnTheSameStreet;
	}

	public static int getNextPositionWithInstruction(
			int currentWalkingPosition, List<WalkingPosition> positions) {
		int nextPositionWithInstruction = 100000;
		if (positions != null && currentWalkingPosition < positions.size() - 1) {
			for (int i = currentWalkingPosition+1; i < positions.size(); i++) {
				if (positions.get(i) != null
						&& positions.get(i).getInstruction() != null) {
					return i;
				}
			}
		}
		return nextPositionWithInstruction;
	}

	public static double distance(WalkingPosition walkingPosition,
			WalkingPosition walkingPosition2) {
		double distance = 10000000;
		if (walkingPosition != null && walkingPosition2 != null
				&& walkingPosition.getPoint() != null
				&& walkingPosition2.getPoint() != null) {
			GPScoordinateHelper.getDistanceBetweenPoints(walkingPosition
					.getPoint().getLatitude(), walkingPosition2.getPoint()
					.getLatitude(), walkingPosition.getPoint().getLongitude(),
					walkingPosition2.getPoint().getLongitude());
		}
		return distance;
	}
}
