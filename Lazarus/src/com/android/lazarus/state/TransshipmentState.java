package com.android.lazarus.state;

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

}
