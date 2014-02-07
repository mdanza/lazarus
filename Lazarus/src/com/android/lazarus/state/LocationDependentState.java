package com.android.lazarus.state;

import android.location.Location;

import com.android.lazarus.VoiceInterpreterActivity;

public abstract class LocationDependentState extends AbstractState {

	float minimumAccuraccy;
	Location position;
	boolean enoughAccuraccy = true;
	String notEnoughAccuracyMessage = "No se puede obtener su posición actual con la suficiente precisión, por favor encienda el g p s y el wi fi, en caso de tenerlos encendidos ya diríjase a un lugar abierto,, ";
	protected boolean fromNotEnoughAccuraccyMessage = false;
	protected String accuraccyObtainedMessage = "Se pudo obtener su ubicación, la última instrucción fue, ";
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

	public void positionChanged(Location position){
		if (position == null) {
			fromNotEnoughAccuraccyMessage = true;
			if (!message.equals(notEnoughAccuracyMessage)) {
				oldMessage = message;
				message = notEnoughAccuracyMessage;
				context.speak(notEnoughAccuracyMessage);
			}
		} else {
			if (!(position.getAccuracy() < minimumAccuraccy)) {
				if (!message.equals(notEnoughAccuracyMessage)) {
					oldMessage = message;
					message = notEnoughAccuracyMessage;
					context.speak(notEnoughAccuracyMessage);
				}
				fromNotEnoughAccuraccyMessage = true;
				enoughAccuraccy = false;
			} else {
				if (fromNotEnoughAccuraccyMessage) {
					message = oldMessage;
					context.speak(accuraccyObtainedMessage + " " + oldMessage);
					fromNotEnoughAccuraccyMessage = false;
				}
				enoughAccuraccy = true;
				this.position = position;
				setPosition(position);
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

	protected abstract void giveInstructions();
}
