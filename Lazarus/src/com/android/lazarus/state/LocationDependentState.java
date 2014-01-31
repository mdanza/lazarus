package com.android.lazarus.state;

import java.util.List;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;

public abstract class LocationDependentState extends AbstractState {

	float minimumAccuraccy;
	Location position;
	boolean enoughAccuraccy = true;
	String notEnoughAccuracyMessage = "No se puede obtener su posición actual con la suficiente precisión, por favor encienda el g p s, en caso de tenerlo encendido ya por favor diríjase a un lugar abierto,, ";
	protected boolean fromNotEnoughAccuraccyMessage = false;
	protected String accuraccyObtainedMessage = "Se pudo obtener su ubicación, ";
	protected String oldMessage = "";
	
	public LocationDependentState(VoiceInterpreterActivity context) {
		super(context);
	}

	LocationDependentState(VoiceInterpreterActivity context,
			float minimumAccuraccy) {
		super(context);
		this.minimumAccuraccy = minimumAccuraccy;
		loadFirstTimePosition();
	}

	public float getMinimumAccuraccy() {
		return minimumAccuraccy;
	}

	public void setMinimumAccuraccy(float minimumAccuraccy) {
		this.minimumAccuraccy = minimumAccuraccy;
	}

	public Location getPosition() {
		return position;
	}

	public void loadFirstTimePosition() {
		setFirstTimePosition(context.getLocationListener().getLocation());
	}

	public void setFirstTimePosition(Location position) {
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

	public abstract void setPosition(Location position);

	public boolean isEnoughAccuraccy() {
		return enoughAccuraccy;
	}

	public void setEnoughAccuraccy(boolean enoughAccuraccy) {
		this.enoughAccuraccy = enoughAccuraccy;
	}

	@Override
	protected void handleResults(List<String> results) {
		// TODO Auto-generated method stub

	}

	protected abstract void giveInstructions();
}
