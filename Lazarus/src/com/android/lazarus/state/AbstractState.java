package com.android.lazarus.state;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.CloseLocationData;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.AddressServiceAdapterStub;

public abstract class AbstractState implements State {

	protected VoiceInterpreterActivity context;
	AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterStub();

	protected String message;

	protected String defaultMessage;

	public String getMessage() {
		return message;
	}

	AbstractState(VoiceInterpreterActivity context) {
		this.context = context;
		this.message = defaultMessage;
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
		if(location==null){
		    return "No se puede obtener informaci�n de su posici�n, por favor encienda el g p s";
		}else{
			CloseLocationData closeLocationData = addressServiceAdapter.getCloseLocation(location.getLatitude(),location.getLongitude());
			return "Usted se encuentra en la concha de su hermana. ";
		}
	}

	public void setResults(List<String> results) {
		results = stripAccents(results);
		if (stringPresent(results, "donde estoy")) {
			this.message = getWhereAmIMessage() + this.defaultMessage;
			return;
		}
		if (stringPresent(results, "cancelar")) {
			String className = this.getClass().getName();
			Class<?> clazz;
			try {
				clazz = Class.forName(className);
				Constructor<?> constructor = clazz
						.getConstructor(VoiceInterpreterActivity.class);
				State newState = (State) constructor.newInstance(this.context);
				this.context.setState(newState);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if (stringPresent(results, "menu")) {
			initializeMainMenu();
			return;
		}
		handleResults(results);
	}

	private ArrayList<String> stripAccents(List<String> results) {
		ArrayList<String> stripedStrings = new ArrayList<String>();
		for (String result : results) {
			result = result.toLowerCase();
			result = result.replace("�", "a");
			result = result.replace("�", "e");
			result = result.replace("�", "i");
			result = result.replace("�", "o");
			result = result.replace("�", "u");
			result = result.replace("�", "u");
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
		for (Integer digit : numbers) {
			digits = digit + " ";
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

	private int toDigit(String string) {
		for(int i = 0;i<10;i++){
			if(isTheSameDigit(string, i)){
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
		if(i==separated.length || (i==separated.length-1 && i!=0)){
			return true;
		}else{
			return false;
		}
	}
	
}
