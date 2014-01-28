package com.android.lazarus.state;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.AddressServiceAdapterImpl;

public abstract class AbstractState implements State {

	protected VoiceInterpreterActivity context;
	AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterImpl();
	protected String generalInstructions = "Usted puede decir en cualquier momento,, ayuda,, dónde estóii,, cancelar,, o menú,, Si dice ayuda,, obtendrá más instrucciones,, si dice dónde estóii,, obtendrá información de lugares cercanos,, si dice cancelar,, se iniciará nuevamente la acción que esté realizando,, si dice menú,, será dirigido al menú principal,, si quiere que se le repita un mensaje, debe tocar la pantalla,, para escuchar nuevamente estas instrucciones diga ayuda,, ";
	protected String stateInstructions = "";
	
	private WhereAmITask whereAmITask = new WhereAmITask();

	protected String message;

	protected String defaultMessage;

	public String getMessage() {
		return message;
	}

	public AbstractState(VoiceInterpreterActivity context) {
		this.context = context;
	}

	protected boolean stringPresent(List<String> results, String search) {
		boolean stringPresent = false;
		for (String result : results) {
			if (search.equals(result)) {
				stringPresent = true;
			}
		}
		return stringPresent;
	}

	protected String getWhereAmIMessage() {
		Location location = this.context.getLocationListener().getLocation();
		if (location == null) {
			return "No se puede obtener información de su posición, por favor encienda el g p s,,";
		} else {
			if (whereAmITask.getStatus() != AsyncTask.Status.RUNNING) {
				if (whereAmITask.getStatus() == AsyncTask.Status.PENDING) {
					whereAmITask.execute(new Location[] { location });
				} else {
					if (whereAmITask.getStatus() == AsyncTask.Status.FINISHED) {
						whereAmITask = new WhereAmITask();
						whereAmITask.execute(new Location[] { location });
					}
				}
			}
			return "Calculando su ubicación,,";
		}
	}

	public void setResults(List<String> results) {
		if (results != null) {
			results = stripAccents(results);
			if (stringPresent(results, "donde estoy")) {
				this.message = getWhereAmIMessage();
				return;
			}
			if (stringPresent(results, "cancelar")) {
				this.restartState();
			}
			if (stringPresent(results, "menu")) {
				if (context.getToken() != null) {
					initializeMainMenu();
				}
				return;
			}
			if (stringPresent(results, "ayuda")) {
				message = generalInstructions + message;
				return;
			}
			handleResults(results);
		}
	}

	protected abstract void restartState();

	private ArrayList<String> stripAccents(List<String> results) {
		ArrayList<String> stripedStrings = new ArrayList<String>();
		for (String result : results) {
			result = result.toLowerCase();
			result = result.replace("á", "a");
			result = result.replace("é", "e");
			result = result.replace("í", "i");
			result = result.replace("ó", "o");
			result = result.replace("ú", "u");
			result = result.replace("ü", "u");
			stripedStrings.add(result);
		}
		return stripedStrings;
	}

	abstract protected void handleResults(List<String> results);

	private boolean containsAnyDigit(String result) {
		boolean numberPresent = false;
		for (int i = 0; i < 10; i++) {
			if (isTheSameDigit(result, i)) {
				numberPresent = true;
			}
		}
		return numberPresent;
	}

	protected List<Integer> getNumbersByDigits(int number) {
		ArrayList<Integer> numbersByPosition = new ArrayList<Integer>();
		while (number > 0) {
			numbersByPosition.add(number % 10);
			number = number / 10;
		}
		return numbersByPosition;
	}

	protected String getStringDigits(int number) {
		List<Integer> numbers = getNumbersByDigits(number);
		String digits = "";
		for (int i = 0; i < numbers.size(); i++) {
			Integer digit = numbers.get(i);
			if (i == 0) {
				digits = Integer.toString(digit);
			} else {
				digits = digit + " " + digits;
			}
		}
		return digits;
	}

	protected boolean containsNumber(List<String> results, int number) {
		boolean containsNumber = false;
		List<Integer> digits = getNumbersByDigits(number);
		for (String result : results) {
			String[] words = result.split(" ");
			if (words.length == digits.size()) {
				boolean isTheSameNumber = true;
				for (int i = 0; i < words.length; i++) {
					if (!isTheSameDigit(words[i], digits.get(i))) {
						isTheSameNumber = false;
					}
				}
				if (isTheSameNumber) {
					containsNumber = true;
				}
			}
		}
		return containsNumber;

	}

	/**
	 * Only works for one digit number
	 */
	private boolean isTheSameDigit(String result, int number) {
		boolean numberPresent = false;
		switch (number) {
		case 0:
			if ("0".equals(result) || "cero".equals(result))
				numberPresent = true;
			break;
		case 1:
			if ("1".equals(result) || "uno".equals(result))
				numberPresent = true;
			break;
		case 2:
			if ("2".equals(result) || "dos".equals(result))
				numberPresent = true;
			break;
		case 3:
			if ("3".equals(result) || "tres".equals(result))
				numberPresent = true;
			break;
		case 4:
			if ("4".equals(result) || "cuatro".equals(result))
				numberPresent = true;
			break;
		case 5:
			if ("5".equals(result) || "cinco".equals(result))
				numberPresent = true;
			break;
		case 6:
			if ("6".equals(result) || "seis".equals(result))
				numberPresent = true;
			break;
		case 7:
			if ("7".equals(result) || "siete".equals(result))
				numberPresent = true;
			break;
		case 8:
			if ("8".equals(result) || "ocho".equals(result))
				numberPresent = true;
			break;
		case 9:
			if ("9".equals(result) || "nueve".equals(result))
				numberPresent = true;
			break;
		}
		return numberPresent;
	}

	protected void initializeMainMenu() {
		MainMenuState mainMenuState = new MainMenuState(this.context);
		context.setState(mainMenuState);
	}

	protected List<String> getAddressNumberString(String string) {
		String[] separated = string.split(" ");
		int i = 0;
		StringBuilder numberBuilder = new StringBuilder();
		while (i < separated.length && containsAnyDigit(separated[i])) {
			numberBuilder.append(toDigit(separated[i]));
			i++;
		}
		String letter = "";
		if (i < separated.length) {
			letter = separated[i];
		}
		ArrayList<String> address = new ArrayList<String>();
		address.add(numberBuilder.toString());
		address.add(letter);
		return address;
	}

	/**
	 * @param numbers
	 *            List of strings each including possible numbers, for example
	 *            [3 4 5 7 1, 3 6 9 0, uno uno uno]
	 * @return null if there is no number inside numbers, the first number
	 *         encountered otherwise, for the example "34571"
	 */
	protected String getNumber(List<String> numbers) {
		String toReturn = null;
		for (String number : numbers) {
			if (getNumber(number) != null) {
				return number;
			}
		}
		return toReturn;
	}

	protected String getNumber(String number) {
		String toReturn = null;
		String[] separated = number.split(" ");
		int i = 0;
		StringBuilder numberBuilder = new StringBuilder();
		while (i < separated.length && containsAnyDigit(separated[i])) {
			numberBuilder.append(toDigit(separated[i]));
			i++;
		}
		if (i < separated.length) {
			toReturn = null;
		} else {
			return numberBuilder.toString();
		}

		return toReturn;
	}

	private int toDigit(String string) {
		for (int i = 0; i < 10; i++) {
			if (isTheSameDigit(string, i)) {
				return i;
			}
		}
		throw new IllegalArgumentException("not a digit");
	}

	protected boolean isAddressNumber(String string) {
		String[] separated = string.split(" ");
		int i = 0;
		while (i < separated.length && containsAnyDigit(separated[i])) {
			i++;
		}
		if (i == separated.length || (i == separated.length - 1 && i != 0)) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean isNumber(String string) {
		String[] separated = string.split(" ");
		int i = 0;
		while (i < separated.length && containsAnyDigit(separated[i])) {
			i++;
		}
		if (i == separated.length) {
			return true;
		} else {
			return false;
		}
	}

	private class WhereAmITask extends AsyncTask<Location, Void, Void> {

		@Override
		protected Void doInBackground(Location... args) {
			Location location = args[0];
			CloseLocationData closeLocationData = null;
			closeLocationData = addressServiceAdapter.getCloseLocation(
					context.getToken(),
					location.getLatitude() + "," + location.getLongitude());
			if (closeLocationData != null) {
				String corner = closeLocationData.getClosestCorner()
						.getFirstStreetName()
						.equals(closeLocationData.getClosestStreet().getName()) ? closeLocationData
						.getClosestCorner().getSecondStreetName()
						: closeLocationData.getClosestCorner()
								.getFirstStreetName();
				message = "Usted se encuentra en, "
						+ closeLocationData.getClosestStreet().getName()
						+ ", esquina, " + corner + ",,";
			} else
				message = "No se pudo obtener información sobre su posición,,";
			message += defaultMessage;
			context.speak(message);
			return null;
		}

	}
	
	public String getHelpMessage(){
		return stateInstructions + ", " + "para obtener más ayuda diga ayuda, ";
	}

}
