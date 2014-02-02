package com.android.lazarus.state;

import java.util.List;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.helpers.GPScoordinateHelper;
import com.android.lazarus.model.Point;

public class DestinationSetState extends LocationDependentState {

	Point destination;
	boolean firstIntructionPassed = false;
	boolean fromFavourite = false;
	boolean hasFavourites = false;
	private static final int NEEDED_ACCURACY = 200;

	public DestinationSetState(VoiceInterpreterActivity context,
			Point destination, boolean fromFavourite, boolean hasFavourites) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		this.fromFavourite = fromFavourite;
		this.hasFavourites = hasFavourites;
		message = "";
	}

	public DestinationSetState(VoiceInterpreterActivity context) {
		super(context);
	}

	@Override
	protected void handleResults(List<String> results) {
		if (this.containsNumber(results, 1)) {
			BusDirectionsState busDirectionsState = new BusDirectionsState(
					this.context, destination, fromFavourite, hasFavourites);
			this.context.setState(busDirectionsState);
			return;
		}
		if (this.containsNumber(results, 2)) {
			WalkingDirectionsState walkingDirectionsState = new WalkingDirectionsState(
					this.context, destination);
			this.context.setState(walkingDirectionsState);
			return;
		}
		if (this.containsNumber(results, 3)) {
			AddToFavouriteState addToFavouriteState = new AddToFavouriteState(
					context, destination);
			this.context.setState(addToFavouriteState);
			return;
		}
	}

	@Override
	protected void giveInstructions() {
		if (!firstIntructionPassed && destination != null && position != null) {
			firstIntructionPassed = true;
			message = generateMessage();
			context.speak(message);
		}
	}

	private String generateMessage() {
		String newMessage = "";
		if (destination != null && position != null) {
			Double approximateDistance = GPScoordinateHelper
					.getDistanceBetweenPoints(this.position.getLatitude(),
							destination.getLatitude(),
							this.position.getLongitude(),
							destination.getLongitude());
			approximateDistance = approximateDistance / 1000;
			approximateDistance = Math.floor(approximateDistance * 10) / 10;
			newMessage = "Usted se encuentra aproximadamente a "
					+ approximateDistance
					+ " kilómetros del destino, si quiere ir en bus diga uno, si quiere ir a pie diga dos, ";
			if (!fromFavourite && hasFavourites) {
				newMessage = newMessage
						+ "para agregarlo a favoritos diga tres";
			}
			if (!hasFavourites) {
				newMessage = newMessage
						+ "usted puede agregar este destino a favoritos, si lo hace podrá seleccionarlo en otras oportunidades por un nombre más corto,, para agregar este destino a favoritos diga tres, ";
			}
		}
		return newMessage;
	}

	@Override
	protected void cancel() {
		DestinationSetState destinationSetState = new DestinationSetState(
				context, destination, fromFavourite, hasFavourites);
		context.setState(destinationSetState);
	}

	@Override
	public void setPosition(Location position) {
		if (position == null) {
			fromNotEnoughAccuraccyMessage  = true;
			oldMessage = message;
			message = notEnoughAccuracyMessage;
			context.speak(notEnoughAccuracyMessage);
		} else {
			if (!(position.getAccuracy() < minimumAccuraccy)) {
				oldMessage = message;
				message = notEnoughAccuracyMessage;
				fromNotEnoughAccuraccyMessage = true;
				enoughAccuraccy = false;
				context.speak(notEnoughAccuracyMessage);
			} else {
				if(fromNotEnoughAccuraccyMessage){
					message = oldMessage;
					context.speak(accuraccyObtainedMessage+" "+oldMessage);
					fromNotEnoughAccuraccyMessage = false;
				}
				enoughAccuraccy = true;
				this.position = position;
				giveInstructions();
			}
		}
	}

	@Override
	public void onAttach() {
		giveInstructions();
	}
	
	@Override
	protected void cancelAsyncTasks() {	
	}

}