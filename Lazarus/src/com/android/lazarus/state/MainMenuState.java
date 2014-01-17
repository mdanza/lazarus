package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Favourite;
import com.android.lazarus.serviceadapter.AddressServiceAdapter;
import com.android.lazarus.serviceadapter.AddressServiceAdapterImpl;
import com.android.lazarus.serviceadapter.FavouritesReportingServiceAdapter;
import com.android.lazarus.serviceadapter.FavouritesReportingServiceAdapterImpl;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterImpl;

public class MainMenuState extends AbstractState {

	AddressServiceAdapter addressServiceAdapter = new AddressServiceAdapterImpl();
	UserServiceAdapter userServiceAdapter = new UserServiceAdapterImpl();
	FavouritesReportingServiceAdapter favouritesReportingServiceAdapter = new FavouritesReportingServiceAdapterImpl();
	String defaultMessage = "Diga el nombre de la calle a la que quiere dirigirse, o nombre favorito de destino, para más opciones diga más";
	int position = 0;
	List<String> streets = null;
	Favourite favourite = null;
	boolean toChooseStreet = false;
	boolean toConfirmFavourite = false;
	List<String> firstResults;

	public MainMenuState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		this.message = defaultMessage;
	}

	public MainMenuState(VoiceInterpreterActivity context, String initialText) {
		super(context);
		this.context = context;
		this.message = initialText + defaultMessage;
	}

	public void handleResults(List<String> results) {
		if (wantsMoreMainMenu(results)) {
			initializeMoreMainMenu();
			return;
		}
		// for each position checks if there is an available street or favourite
		// place
		// if there is none or user asks for more results, go to next position
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
			PossibleDestinationTask possibleDestinationTask = new PossibleDestinationTask();
			String[] args = new String[1];
			args[0] = firstResults.get(position);
			message = "";
			possibleDestinationTask.execute(args);
		}
		if (firstResults != null && position == firstResults.size()) {
			if (position != 0 && !toChooseStreet && !toConfirmFavourite) {
				this.message = "No se han encontrado resultados."
						+ defaultMessage;
			}
			if (position != 0 && (toChooseStreet || toConfirmFavourite)) {
				this.message = "No se han encontrado otros resultados."
						+ defaultMessage;
			}
			position = 0;
			firstResults = null;
			context.sayMessage();
			return;
		}
		if (stringPresent(results, "si") && toConfirmFavourite) {
			DestinationSetState destinationSetState = new DestinationSetState(
					this.context, favourite.getPoint(), true);
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

	private Favourite getFavourite(String string, List<Favourite> favourites) {
		if (favourites != null) {
			for (Favourite favourite : favourites) {
				if (favourite != null && favourite.getName().equals(string)) {
					return favourite;
				}
			}
		}
		return null;

	}

	private class PossibleDestinationTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			streets = addressServiceAdapter.getPossibleStreets(
					context.getToken(), args[0]);
			List<Favourite> favourites = favouritesReportingServiceAdapter
					.getFavourites(context.getToken());
			favourite = getFavourite(args[0], favourites);
			if (favourite == null && (streets == null || streets.isEmpty())) {
				goToNextPosition();
				return null;
			}
			if (favourite != null && !toChooseStreet && !toConfirmFavourite) {
				toConfirmFavourite = true;
				message = "Desea dirigirse a " + favourite.getName() + "?";
				context.speak(message);
				return favourite.getName();
			}
			if (streets != null && !streets.isEmpty() && !toChooseStreet
					&& !toConfirmFavourite) {
				toChooseStreet = true;
				message = "";
				for (int i = 1; i < streets.size() + 1; i++) {
					message = message + "Si desea dirigirse a "
							+ streets.get(i - 1) + " diga "
							+ getStringDigits(i) + ",";
				}
				String finalMessage = " para obtener otros resultados posibles diga más";
				message = message + finalMessage;
				context.speak(message);
				return null;
			}

			return null;
		}

	}

	@Override
	protected void restartState() {
		MainMenuState mainMenuState = new MainMenuState(context);
		this.context.setState(mainMenuState);

	}

}
