package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Point;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.AddressServiceAdapterImpl;

public class StreetSetState extends AbstractState {

	String firstStreet;
	AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterImpl();
	String defaultMessage = "Diga el nombre de otra calle, o el número de puerta, dígito a dígito, ";
	int position = 0;
	List<String> streets = null;
	List<String> firstResults;
	private boolean firstTime = true;
	private boolean toConfirmDoorNumber = false;
	private boolean toChooseStreet = false;
	private boolean toConfirmSecondStreet = false;
	private String secondStreet = null;
	private String addressNumber = null;
	private boolean passedFirstTime = false;
	GetStreetNameTask getStreetNameTask = new GetStreetNameTask();
	private boolean hasFavourites = false;

	public StreetSetState(VoiceInterpreterActivity context, String street, boolean hasFavourites) {
		super(context);
		firstStreet = street;
		this.message = defaultMessage;
		this.hasFavourites = hasFavourites;
	}

	@Override
	protected void handleResults(List<String> results) {
		if (firstTime) {
			firstTime = false;
			firstResults = results;
			checkForNumberOrCorner();
			return;
		}
		passedFirstTime = true;
		if (!toChooseStreet && !firstTime && !toConfirmDoorNumber
				&& !toConfirmSecondStreet) {
			if (stringPresent(results, "mas")) {
				goToNextPosition();
				return;
			}
		}
		if (toConfirmDoorNumber) {
			if (stringPresent(results, "si")) {
				goToDestinationSetState();
				return;
			}
			if (stringPresent(results, "no")) {
				goToNextPosition();
				return;
			}
		}
		if (toChooseStreet) {
			for (int i = 1; i < streets.size() + 1; i++) {
				if (containsNumber(results, i)) {
					secondStreet = streets.get(i - 1);
					toConfirmSecondStreet = true;
					toChooseStreet = false;
					this.message = "¿Desea ir a " + firstStreet + " esquina "
							+ secondStreet + "?";
					return;
				}
			}
			if (stringPresent(results, "mas")) {
				goToNextPosition();
			}
		}
		if (toChooseStreet && stringPresent(results, "mas")) {
			goToNextPosition();
			return;
		}
		if (toConfirmSecondStreet) {
			if (stringPresent(results, "si")) {
				goToDestinationSetState();
				return;
			}
			if (stringPresent(results, "no")) {
				checkForNumberOrCorner();
			}
		}

	}

	private void goToDestinationSetState() {
		SetDestinationTask setDestinationTask = new SetDestinationTask();
		message = "";
		if (setDestinationTask.getStatus() != AsyncTask.Status.RUNNING) {
			if (setDestinationTask.getStatus() == AsyncTask.Status.PENDING) {
				setDestinationTask.execute();
			} else {
				if (setDestinationTask.getStatus() == AsyncTask.Status.FINISHED) {
					setDestinationTask = new SetDestinationTask();
					setDestinationTask.execute();
				}
			}
		}
	}

	private void checkForNumberOrCorner() {
		if (position == firstResults.size()) {
			if (passedFirstTime) {
				this.message = "No se han encontrado otros resultados posibles, ";
			} else {
				this.message = "No se han encontrado resultados, ";
			}
			this.message = message + defaultMessage;
			resetData();
			return;
		}
		if (isAddressNumber(firstResults.get(position))) {
			toConfirmDoorNumber = true;
			this.message = "Desea ir a "
					+ firstStreet
					+ " "
					+ getStringDigits(Integer.valueOf(getAddressNumberString(
							firstResults.get(position)).get(0))) + " "
					+ getAddressNumberString(firstResults.get(position)).get(1)
					+ "?";
			addressNumber = firstResults.get(position);
			return;
		}
		message = "";
		if (getStreetNameTask.getStatus() != AsyncTask.Status.RUNNING) {
			if (getStreetNameTask.getStatus() == AsyncTask.Status.PENDING) {
				getStreetNameTask.execute();
			} else {
				if (getStreetNameTask.getStatus() == AsyncTask.Status.FINISHED) {
					GetStreetNameTask getStreetNameTask = new GetStreetNameTask();
					getStreetNameTask.execute();
				}
			}
		}else{
			message = "Espere mientras cargamos sus datos";
		}
	}

	private void goToNextPosition() {
		position++;
		streets = null;
		secondStreet = null;
		toConfirmDoorNumber = false;
		toChooseStreet = false;
		toConfirmSecondStreet = false;
		addressNumber = null;
		checkForNumberOrCorner();
	}

	private void resetData() {
		position = 0;
		streets = null;
		secondStreet = null;
		toConfirmDoorNumber = false;
		toChooseStreet = false;
		toConfirmSecondStreet = false;
		addressNumber = null;
		firstTime = true;
		passedFirstTime = false;
	}

	private class GetStreetNameTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			message = "Espere mientras cargamos sus datos";
			streets = addressServiceAdapter.getPossibleStreets(
					context.getToken(), firstResults.get(position));
			if (streets != null && !streets.isEmpty()) {
				toChooseStreet = true;
				message = "";
				for (int i = 1; i < streets.size() + 1; i++) {
					message = message + "Si desea dirigirse a "
							+ streets.get(i - 1) + " esquina " + firstStreet
							+ " diga " + getStringDigits(i) + ",";
				}
				String finalMessage = " para obtener otros resultados posibles diga más";
				message = message + finalMessage;
				context.speak(message);
				return null;
			}
			if (position < firstResults.size()) {
				goToNextPosition();
			}
			return null;
		}

	}

	private class SetDestinationTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			message = "Espere mientras cargamos los datos";
			Point destination = null;
			if (secondStreet != null) {
				destination = addressServiceAdapter.getCorner(
						context.getToken(), firstStreet, secondStreet);
			}
			if (addressNumber != null) {
				List<String> address = getAddressNumberString(firstResults
						.get(position));
				int number = Integer.parseInt(address.get(0));
				destination = addressServiceAdapter.getByDoorNumber(
						context.getToken(),
						firstStreet,
						number,
						getAddressNumberString(firstResults.get(position)).get(
								1));
			}
			if (destination == null) {
				resetData();
				message = "No se han encontrado resultados, " + defaultMessage;
				context.speak(message);
			} else {
				DestinationSetState destinationSetState = new DestinationSetState(
						context, destination, false, hasFavourites);
				context.setState(destinationSetState);
			}
			return null;
		}
	}

	@Override
	protected void cancel() {
		MainMenuState mainMenuState = new MainMenuState(context);
		context.setState(mainMenuState);
	}
}
