package com.android.lazarus.state;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;

public abstract class AbstractState implements State {

	protected VoiceInterpreterActivity context;

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
		return "Usted se encuentra en la concha de su hermana. ";
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
				Constructor<?> constructor = clazz.getConstructor(VoiceInterpreterActivity.class);
				State newState =(State) constructor.newInstance(this.context);
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
		}
		handleResults(results);
	}

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

	/**
	 * Only works for one digit number
	 * 
	 * @param results
	 *            list to check if number (or written form of number) is present
	 *            ("2" or "dos") will be searched for in case number is 2
	 * @param number
	 * @return
	 */
	protected boolean numberPresent(List<String> results, int number) {
		boolean numberPresent = false;
		for (String result : results) {
			switch (number) {
			case 0:
				if ("0".equals(result) || "cero".equals(result))
					numberPresent = true;
			case 1:
				if ("1".equals(result) || "uno".equals(result))
					numberPresent = true;
			case 2:
				if ("2".equals(result) || "dos".equals(result))
					numberPresent = true;
			case 3:
				if ("3".equals(result) || "tres".equals(result))
					numberPresent = true;
			case 4:
				if ("4".equals(result) || "cuatro".equals(result))
					numberPresent = true;
			case 5:
				if ("5".equals(result) || "cinco".equals(result))
					numberPresent = true;
			case 6:
				if ("6".equals(result) || "seis".equals(result))
					numberPresent = true;
			case 7:
				if ("7".equals(result) || "siete".equals(result))
					numberPresent = true;
			case 8:
				if ("8".equals(result) || "ocho".equals(result))
					numberPresent = true;
			case 9:
				if ("9".equals(result) || "nueve".equals(result))
					numberPresent = true;
			}
		}
		return numberPresent;
	}

}
