package com.android.lazarus.state;

import java.util.List;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Favourite;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.AddressServiceAdapterStub;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterStub;

public class StreetSetState extends AbstractState {

	StreetSetState(VoiceInterpreterActivity context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public StreetSetState(VoiceInterpreterActivity context, String string) {
		super(context);
	}

	@Override
	protected void handleResults(List<String> results) {
		// TODO Auto-generated method stub
		
	}
	/*
	String firstStreet;
	AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterStub();
	String defaultMessage = "Diga el nombre de otra calle o el número de puerta, ";
	int position = 0;
	List<String> streets = null;
	boolean toChooseStreet = false;
	List<String> firstResults;

	StreetSetState(VoiceInterpreterActivity context) {
		super(context);
	}

	public StreetSetState(VoiceInterpreterActivity context, String street) {
		super(context);
		firstStreet = street;
		this.message = defaultMessage;
	}

	@Override
	protected void handleResults(List<String> results) {
		if (wantsMoreResults(results) && firstResults != null
				&& position < firstResults.size()) {
			goToNextPosition();
			return;
		}
		if ((firstResults == null || position < firstResults.size())
				&& !toChooseStreet && !toConfirmFavourite) {
			if (firstResults == null) {
				firstResults = results;
			}
			streets = addressServiceAdapter.getPossibleStreets(firstResults
					.get(position));
			favourite = userServiceAdapter.getFavourite(firstResults
					.get(position));
			if (favourite == null && (streets == null || streets.isEmpty())) {
				goToNextPosition();
				return;
			}
			if (favourite != null && !toChooseStreet && !toConfirmFavourite) {
				toConfirmFavourite = true;
				this.message = "¿Desea dirigirse a " + favourite.getName()
						+ "?";
				return;
			}
			if (streets != null && !streets.isEmpty() && !toChooseStreet
					&& !toConfirmFavourite) {
				toChooseStreet = true;
				this.message = "";
				for (int i = 1; i < streets.size() + 1; i++) {
					this.message = message + "Si desea dirigirse a "
							+ streets.get(i - 1) + " diga "
							+ getStringDigits(i) + ",";
				}
				String finalMessage = " para obtener otros resultados posibles diga más";
				this.message = message + finalMessage;
				return;
			}

		}
		if (firstResults != null && position == firstResults.size()) {
			if (position != 0 && !toChooseStreet && !toConfirmFavourite){
				this.message = "No se han encontrado resultados."
						+ defaultMessage;
			}
			if(position !=0 && (toChooseStreet || toConfirmFavourite)){
				this.message = "No se han encontrado otros resultados."
						+ defaultMessage;
			}
			position = 0;
			return;
		}
		if (stringPresent(results, "si") && toConfirmFavourite) {
			DestinationSetState destinationSetState = new DestinationSetState(
					this.context, favourite.getPoint());
			this.context.setState(destinationSetState);
			return;
		}
		if (streets != null && toChooseStreet) {
			for (int i = 1; i < streets.size() + 1; i++) {
				if (containsNumber(results, i)) {
					StreetSetState streetSetState = new StreetSetState(
							this.context, streets.get(i - 1));
					this.context.setState(streetSetState);
				}
			}
			return;
		}
		

	}
	
	private void initializeMoreMainMenu() {
		MoreMainMenuState moreMainMenuState = new MoreMainMenuState(
				this.context);
		context.setState(moreMainMenuState);
		
	}

	private void goToNextPosition() {
		position++;
		favourite = null;
		streets = null;
		toChooseStreet = false;
		toConfirmFavourite = false;
		setResults(firstResults);
	}

	private boolean wantsMoreResults(List<String> results) {
		return (toChooseStreet && stringPresent(results, "mas"))
				|| (toConfirmFavourite && stringPresent(results, "no"));
	}

	private boolean wantsMoreMainMenu(List<String> results) {
		return stringPresent(results, "mas") && !toChooseStreet
				&& !toConfirmFavourite;
	}
	*/

}
