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
	private boolean toConfirmName;
	private String name;
	private List<Favourite> favourites;
	private FavouritesReportingServiceAdapter favouritesReportingServiceAdapter = new FavouritesReportingServiceAdapterImpl();
	private List<String> possibleNames = null;
	private boolean choosingToGoToFavourite = false;

	public AddToFavouriteState(VoiceInterpreterActivity context) {
		super(context);
	}

	public AddToFavouriteState(VoiceInterpreterActivity context, Point point) {
		super(context);
		this.point = point;
		this.defaultMessage = "Elija el nombre para este destino favorito";
		this.message = defaultMessage;
		loadFavourites();
	}

	private void loadFavourites() {
		GetFavouritesTask getFavouritesTask = new GetFavouritesTask();
		String[] args = new String[1];
		args[0] = context.getToken();
		getFavouritesTask.doInBackground(args);
	}

	@Override
	protected void handleResults(List<String> results) {
		if (!toConfirmName && position < results.size()) {
			if (possibleNames == null) {
				possibleNames = results;
			}
			name = possibleNames.get(position);
			this.message = "¿Desea que el destino favorito se llame " + name
					+ "?";
			toConfirmName = true;
			return;
		}
		if (toConfirmName) {
			toConfirmName = false;
			if (stringPresent(results, "si")) {
				if (favouriteWithName(favourites, name)) {
					this.message = "Ya existe un favorito con este nombre, por favor elija otro";
					resetData();
				} else {
					String[] args = new String[3];
					args[0] = context.getToken();
					args[1] = point.getLatitude() + "," + point.getLongitude();
					args[2] = name;
					AddToFavouriteTask addToFavouriteTask = new AddToFavouriteTask();
					addToFavouriteTask.doInBackground(args);
				}
			}
			if(stringPresent(results, "no")){
				toConfirmName = true;
				position++;
				this.handleResults(possibleNames);
			}
			return;
		}
		if(!toConfirmName && position==results.size()){
			this.message = "Por favor repita el nombre del favorito que desea agregar";
			resetData();
		}
		if(choosingToGoToFavourite){
			choosingToGoToFavourite = false;
			if(stringPresent(results, "si")){
				DestinationSetState destinationSetState = new DestinationSetState(
						this.context, point, true);
				this.context.setState(destinationSetState);
			}
			if(stringPresent(results, "no")){
				MainMenuState mainMenuState = new MainMenuState(context);
				context.setState(mainMenuState);
			}
		}

	}

	private void resetData() {
		position = 0;
		toConfirmName = false;
		name = null;
		possibleNames = null;
		loadFavourites();

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
	protected void restartState() {
		AddToFavouriteState addToFavouriteState = new AddToFavouriteState(
				context, point);
		context.setState(addToFavouriteState);
	}

	private class AddToFavouriteTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			boolean added = false;
			if (args != null && args.length == 3) {
				added = favouritesReportingServiceAdapter.addToFavourite(
						args[0], args[1], args[2]);
			}
			if (added) {
				message = "Se ha agregado " + args[2]
						+ " a favoritos, ¿desea ir a " + args[2] + "?";
				choosingToGoToFavourite = true;
			} else {
				message = "Ha ocurrido un error, al agregar el favorito, por favor diga el nombre nuevamente";
				resetData();
			}
			return null;
		}
	}

	private class GetFavouritesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			favourites = favouritesReportingServiceAdapter
					.getFavourites(context.getToken());
			return "Done";
		}

	}

}
