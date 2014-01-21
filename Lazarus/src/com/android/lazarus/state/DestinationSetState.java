package com.android.lazarus.state;

import java.util.List;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.helpers.GPScoordinateHelper;
import com.android.lazarus.model.Point;

public class DestinationSetState extends LocationDependentState {

	Point destination;
	boolean firstIntructionPassed;
	boolean fromFavourite = false;
	private static final int NEEDED_ACCURACY = 200;

	public DestinationSetState(VoiceInterpreterActivity context,
			Point destination, boolean fromFavourite) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		this.fromFavourite = fromFavourite;
		giveInstructions();
	}

	public DestinationSetState(VoiceInterpreterActivity context) {
		super(context);
	}

	@Override
	protected void handleResults(List<String> results) {
		if (this.containsNumber(results, 1)) {
			BusDirectionsState busDirectionsState = new BusDirectionsState(
					this.context, destination);
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
			Double approximateDistance = GPScoordinateHelper
					.getDistanceBetweenPoints(this.position.getLatitude(),
							destination.getLatitude(),
							this.position.getLongitude(),
							destination.getLongitude());
			approximateDistance = approximateDistance / 1000;
			approximateDistance = Math.floor(approximateDistance * 10) / 10;
			this.message = "Usted se encuentra aproximadamente a "
					+ approximateDistance
					+ " kilómetros del destino, si quiere ir en bus diga uno, si quiere ir a pie diga dos, ";
			if (!fromFavourite) {
				message = message + "para agregarlo a favoritos diga tres";
			}
			context.speak(this.message);
		}
	}

	@Override
	protected void restartState() {
		DestinationSetState destinationSetState = new DestinationSetState(
				context, destination, fromFavourite);
		context.setState(destinationSetState);

	}
	
	@Override
	public void setPosition(Location position) {

		if (position == null) {
			this.message = notEnoughAccuracyMessage;
			context.speak(this.message);
		} else {
			if (!(position.getAccuracy() < minimumAccuraccy)) {
				enoughAccuraccy = false;
				this.message = notEnoughAccuracyMessage;
				context.speak(this.message);
			} else {
				enoughAccuraccy = true;
				this.position = position;
				giveInstructions();
			}
		}
	}

}