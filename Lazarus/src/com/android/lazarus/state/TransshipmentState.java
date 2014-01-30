package com.android.lazarus.state;

import java.util.List;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.BusRide;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.Transshipment;

public class TransshipmentState extends LocationDependentState {
	private Transshipment transshipment;
	private Point destination;
	private static final int NEEDED_ACCURACY = 50;
	private InternalState state = InternalState.FIRST_ROUTE;
	private boolean sameIntermediateStop;

	private enum InternalState {
		FIRST_ROUTE, SECOND_ROUTE
	}

	public TransshipmentState(VoiceInterpreterActivity context,
			Point destination, Transshipment transshipment) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		this.transshipment = transshipment;
		BusRide rideOne = transshipment.getFirstRoute();
		BusRide rideTwo = transshipment.getSecondRoute();
		if (rideOne.getEndStop().getBusStopLocationCode() == rideTwo
				.getStartStop().getBusStopLocationCode())
			sameIntermediateStop = true;
		else
			sameIntermediateStop = false;
		giveInstructions();
	}

	@Override
	protected void handleResults(List<String> results) {
		giveInstructions();
	}

	@Override
	protected void giveInstructions() {
		if (state.equals(InternalState.FIRST_ROUTE)) {
			context.setState(new BusRideState(
					context,
					transshipment.getSecondRoute().getStartStop().getPoint(),
					transshipment.getFirstRoute(),
					this,
					com.android.lazarus.state.BusRideState.InternalState.WALKING_TO_START_STOP,
					null));
		}
		if (state.equals(InternalState.SECOND_ROUTE)) {
			com.android.lazarus.state.BusRideState.InternalState initialState;
			if (sameIntermediateStop)
				initialState = com.android.lazarus.state.BusRideState.InternalState.SEARCHING_BUS;
			else
				initialState = com.android.lazarus.state.BusRideState.InternalState.WALKING_TO_START_STOP;
			context.setState(new BusRideState(context, destination,
					transshipment.getSecondRoute(), this, initialState, null));
		}
	}

	@Override
	protected void cancel() {
		context.setState(new MainMenuState(context));
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
			}
		}
	}

	public void arrivedToDestination() {
		if (state.equals(InternalState.SECOND_ROUTE)) {
			MainMenuState mainMenuState = new MainMenuState(context);
			context.setState(mainMenuState);
		}
		if (state.equals(InternalState.FIRST_ROUTE)) {
			state = InternalState.SECOND_ROUTE;
			giveInstructions();
		}
	}

}
