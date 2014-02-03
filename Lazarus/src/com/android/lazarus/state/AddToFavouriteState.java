package com.android.lazarus.state;

import java.util.List;

import android.os.AsyncTask;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Favourite;
import com.android.lazarus.model.Point;
import com.android.lazarus.serviceadapter.FavouritesReportingServiceAdapter;
import com.android.lazarus.serviceadapter.FavouritesReportingServiceAdapterImpl;

public class AddToFavouriteState extends AbstractState {

	private Point point;
	private int position = 0;
	private String name;
	private List<Favourite> favourites;
	private FavouritesReportingServiceAdapter favouritesReportingServiceAdapter = new FavouritesReportingServiceAdapterImpl();
	private List<String> possibleNames = null;
	AddToFavouriteTask addToFavouriteTask = new AddToFavouriteTask();
	GetFavouritesTask getFavouritesTask = new GetFavouritesTask();
	private InternalState state = InternalState.LOADING_FAVOURITES;

	private enum InternalState {
		LOADING_FAVOURITES, CHOOSING_NAME, CONFIRM_NAME, CHOOSING_TO_GO_TO_FAVOURITE
	}

	public AddToFavouriteState(VoiceInterpreterActivity context) {
		super(context);
	}

	public AddToFavouriteState(VoiceInterpreterActivity context, Point point) {
		super(context);
		this.point = point;
		this.defaultMessage = "Elija el nombre para este destino favorito";
		loadFavourites();
	}

	private void loadFavourites() {
		String[] args = new String[1];
		args[0] = context.getToken();
		message = "";
		if (getFavouritesTask.getStatus() != AsyncTask.Status.RUNNING) {
			if (getFavouritesTask.getStatus() == AsyncTask.Status.PENDING) {
				getFavouritesTask.execute(args);
			} else {
				if (getFavouritesTask.getStatus() == AsyncTask.Status.FINISHED) {
					getFavouritesTask = new GetFavouritesTask();
					getFavouritesTask.execute(args);
				}
			}
		} else {
			message = "Espere mientras cargamos sus datos";
		}
	}

	@Override
	protected void handleResults(List<String> results) {
		if (state.equals(InternalState.CHOOSING_NAME)) {
			if (possibleNames == null) {
				possibleNames = results;
			}

			name = possibleNames.get(position);
			this.message = "¿Desea que el destino favorito se llame " + name
					+ "?";
			state = InternalState.CONFIRM_NAME;

			return;
		}
		if (state.equals(InternalState.CONFIRM_NAME)) {
			if (stringPresent(results, "si")) {
				if (favouriteWithName(favourites, name)) {
					this.message = "Ya existe un favorito con este nombre, por favor elija otro";
					state = InternalState.CHOOSING_NAME;
					resetData();
				} else {
					String[] args = new String[3];
					message = "";
					args[0] = context.getToken();
					args[1] = point.getLatitude() + "," + point.getLongitude();
					args[2] = name;
					if (addToFavouriteTask.getStatus() != AsyncTask.Status.RUNNING) {
						if (addToFavouriteTask.getStatus() == AsyncTask.Status.PENDING) {
							addToFavouriteTask.execute(args);
						} else {
							if (addToFavouriteTask.getStatus() == AsyncTask.Status.FINISHED) {
								addToFavouriteTask = new AddToFavouriteTask();
								addToFavouriteTask.execute(args);
							}
						}
					} else {
						message = "Espere mientras agregamos el destino a favoritos";
					}
				}
			}
			if (stringPresent(results, "no")) {
				position++;
				state = InternalState.CHOOSING_NAME;
				if (position == results.size()) {
					this.message = "Por favor repita el nombre del favorito que desea agregar";
					resetData();
				} else {
					this.handleResults(possibleNames);
				}
			}
			return;
		}
		if (state.equals(InternalState.CHOOSING_TO_GO_TO_FAVOURITE)) {
			if (stringPresent(results, "si")) {
				DestinationSetState destinationSetState = new DestinationSetState(
						this.context, point, true, true);
				this.context.setState(destinationSetState);
			}
			if (stringPresent(results, "no")) {
				MainMenuState mainMenuState = new MainMenuState(context);
				context.setState(mainMenuState);
			}
			return;
		}
	}

	private void resetData() {
		position = 0;
		name = null;
		possibleNames = null;
	}

	private boolean favouriteWithName(List<Favourite> listOfFavourites,
			String nameOfFavourite) {
		boolean found = false;
		if (listOfFavourites != null && nameOfFavourite != null) {
			for (Favourite favourite : listOfFavourites) {
				if (favourite != null && favourite.getName() != null
						&& favourite.getName().equals(nameOfFavourite))
					found = true;
			}
		}
		return found;
	}

	@Override
	protected void cancel() {
		AddToFavouriteState addToFavouriteState = new AddToFavouriteState(
				context, point);
		context.setState(addToFavouriteState);
	}

	private class AddToFavouriteTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			message = "Espere mientras agregamos el favorito";
			boolean added = false;
			if (args != null && args.length == 3) {
				if (isCancelled())
					return null;
				added = favouritesReportingServiceAdapter.addToFavourite(
						args[0], args[1], args[2]);
			}
			if (added) {
				message = "Se ha agregado " + args[2]
						+ " a favoritos, ¿desea ir a " + args[2] + "?";
				state = InternalState.CHOOSING_TO_GO_TO_FAVOURITE;
			} else {
				message = "Ha ocurrido un error al agregar el favorito, por favor diga el nombre nuevamente";
				resetData();
				state = InternalState.CHOOSING_NAME;
			}
			if (isCancelled())
				return null;
			context.sayMessage();
			return null;
		}
	}

	private class GetFavouritesTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... args) {
			message = "Espere por favor";
			if (isCancelled())
				return null;
			favourites = favouritesReportingServiceAdapter
					.getFavourites(context.getToken());
			state = InternalState.CHOOSING_NAME;
			if (favourites != null && favourites.isEmpty()) {
				favourites = null;
			}
			message = defaultMessage;
			if (isCancelled())
				return null;
			context.speak(message);
			return null;
		}

	}

	@Override
	public void onAttach() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void cancelAsyncTasks() {
		if (getFavouritesTask != null)
			getFavouritesTask.cancel(true);
		if (addToFavouriteTask != null)
			addToFavouriteTask.cancel(true);
	}
}
