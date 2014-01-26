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
	int position = 0;
	List<String> streets = null;
	Favourite favourite = null;
	List<String> firstResults;
	List<Favourite> favourites = null;
	private InternalState state = InternalState.GET_DESTINATION;
	LoadFavouritesTask loadFavouritesTask = new LoadFavouritesTask();
	PossibleDestinationTask possibleDestinationTask = new PossibleDestinationTask();

	private enum InternalState {
		GET_DESTINATION, DESTINATION_SAID, TO_CHOOSE_STREET, TO_CONFIRM_FAVOURITE
	}

	public MainMenuState(VoiceInterpreterActivity context) {
		super(context);
		this.context = context;
		loadFavourites("");
	}

	public MainMenuState(VoiceInterpreterActivity context, String initialText) {
		super(context);
		this.context = context;
		loadFavourites(initialText);
	}

	private void loadFavourites(String initialText) {
		String[] args = new String[2];
		args[0] = context.getToken();
		args[1] = initialText;
		if (loadFavouritesTask.getStatus() != AsyncTask.Status.RUNNING) {
			if (loadFavouritesTask.getStatus() == AsyncTask.Status.PENDING){
				loadFavouritesTask.execute(args);
			}else{
				if(loadFavouritesTask.getStatus() == AsyncTask.Status.FINISHED){
					loadFavouritesTask = new LoadFavouritesTask();
					loadFavouritesTask.execute(args);
				}
			}
		}else{
			message = "Espere mientras cargamos sus datos por favor";
		}
	}

	public void handleResults(List<String> results) {
		if (state.equals(InternalState.GET_DESTINATION)) {
			if (wantsMore(results)) {
				initializeMoreMainMenu();
				return;
			} else {
				state = InternalState.DESTINATION_SAID;
				firstResults = results;
				String[] args = new String[1];
				args[0] = firstResults.get(0);
				message = "";
				if (possibleDestinationTask.getStatus() != AsyncTask.Status.RUNNING) {
					if (possibleDestinationTask.getStatus() == AsyncTask.Status.PENDING){
						possibleDestinationTask.execute(args);
					}else{
						if(possibleDestinationTask.getStatus() == AsyncTask.Status.FINISHED){
							possibleDestinationTask = new PossibleDestinationTask();
							possibleDestinationTask.execute(args);
						}
					}
				}
				return;
			}
		}
		if (state.equals(InternalState.TO_CHOOSE_STREET)) {
			if (wantsMore(results)) {
				goToNextPosition();
				return;
			}
			if (streets != null) {
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
		if (state.equals(InternalState.TO_CONFIRM_FAVOURITE)) {
			if (stringPresent(results, "si")) {
				DestinationSetState destinationSetState = new DestinationSetState(
						this.context, favourite.getPoint(), true);
				this.context.setState(destinationSetState);
				// destinationSetState.handleResults(new
				// ArrayList<String>(Arrays.asList(new String[]
				// {"dos","dos","dos"})));
				return;
			}
			if (stringPresent(results, "no")) {
				goToNextPosition();
			}
		}

	}

	private void initializeMoreMainMenu() {
		MoreMainMenuState moreMainMenuState = new MoreMainMenuState(
				this.context);
		context.setState(moreMainMenuState);

	}

	private void goToNextPosition() {
		position++;
		message = "";
		if (position < firstResults.size()) {
			favourite = null;
			streets = null;
			PossibleDestinationTask possibleDestinationTask = new PossibleDestinationTask();
			message = "";
			if (possibleDestinationTask.getStatus() != AsyncTask.Status.RUNNING) {
				if (possibleDestinationTask.getStatus() == AsyncTask.Status.PENDING){
					possibleDestinationTask.execute(firstResults.get(position));
				}else{
					if(possibleDestinationTask.getStatus() == AsyncTask.Status.FINISHED){
						possibleDestinationTask = new PossibleDestinationTask();
						possibleDestinationTask.execute(firstResults.get(position));
					}
				}
			}
		} else {
			if (state.equals(InternalState.TO_CHOOSE_STREET)) {
				message = "No se han encontrado otros resultados.";
			} else {
				message = "No se han encontrado resultados.";
			}
			MainMenuState mainMenuState = new MainMenuState(context, message);
			context.setState(mainMenuState);
			context.sayMessage();
			return;
		}
	}

	private boolean wantsMore(List<String> results) {
		return (stringPresent(results, "mas"));
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
			message = "Espere mientras cargamos sus resultados";
			streets = addressServiceAdapter.getPossibleStreets(
					context.getToken(), args[0]);
			favourite = getFavourite(args[0], favourites);
			if (favourite == null && (streets == null || streets.isEmpty())) {
				goToNextPosition();
				return null;
			}
			if (favourite != null) {
				state = InternalState.TO_CONFIRM_FAVOURITE;
				message = "Desea dirigirse a " + favourite.getName() + "?";
				context.speak(message);
				return favourite.getName();
			}
			if (streets != null && !streets.isEmpty()) {
				state = InternalState.TO_CHOOSE_STREET;
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

	private class LoadFavouritesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			favourites = favouritesReportingServiceAdapter
					.getFavourites(args[0]);
			String initialMessage = "";
			if(args.length==2 && args[1]!=null){
				initialMessage = args[1];
			}
			if (favourites != null) {
				message = "Diga, Sin el número de puerta, el nombre de la calle a la que quiere dirigirse, o nombre favorito de destino, para más opciones diga más";
			} else {
				message = "Diga, Sin el número de puerta, el nombre de la calle a la que quiere dirigirse, para más opciones diga más";
			}
			defaultMessage = message;
			message = initialMessage + message;
			context.speak(message);
			return message;
		}

	}

	@Override
	protected void restartState() {
		MainMenuState mainMenuState = new MainMenuState(context);
		this.context.setState(mainMenuState);

	}

}
