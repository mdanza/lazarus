package com.android.lazarus.state;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.Transshipment;

public class TransshipmentState extends LocationDependentState {
	private Transshipment transshipment;
	private Point destination;
	private String message = "";
	private static final int NEEDED_ACCURACY = 50;
	private InternalState state = InternalState.WAITING;

	private enum InternalState {
		WAITING
	}

	public TransshipmentState(VoiceInterpreterActivity context,
			Point destination, Transshipment transshipment) {
		super(context, NEEDED_ACCURACY);
		this.destination = destination;
		this.transshipment = transshipment;
		giveInstructions();
	}

	@Override
	protected void giveInstructions() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void restartState() {
		// TODO Auto-generated method stub

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

}
