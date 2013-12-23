package com.android.lazarus.state;

import java.util.List;

import android.location.Location;
import android.speech.tts.TextToSpeech;

import com.android.lazarus.VoiceInterpreterActivity;
import com.android.lazarus.listener.LocationListenerImpl;

public abstract class LocationDependentState extends AbstractState {

	float minimumAccuraccy;
	Location position;
	LocationListenerImpl locationListener;
	boolean enoughAccuraccy = true;
	TextToSpeech tts;

	public LocationDependentState(VoiceInterpreterActivity context) {
		super(context);
	}

	LocationDependentState(VoiceInterpreterActivity context,
			float minimumAccuraccy) {
		super(context);
		this.minimumAccuraccy = minimumAccuraccy;
		this.locationListener = context.getLocationListener();
		tts = this.context.getTts();
		loadPosition();
		giveInstructions();
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

	public void loadPosition() {
		if (locationListener.getLocation() == null) {
			this.message = "No se puede obtener su posición actual, por favor encienda el g p s, en caso de tenerlo encendido ya por favor diríjase a un lugar abierto";
			context.speak(this.message);
		} else {
			if (!(locationListener.getLocation().getAccuracy() < minimumAccuraccy)) {
				enoughAccuraccy = false;
				this.message = "No se puede obtener su posición con exactitud, por favor encienda el g p s, en caso de tenerlo encendido ya por favor dirigase a un lugar abierto";
				context.speak(this.message);
			} else {
				enoughAccuraccy = true;
				this.position = locationListener.getLocation();
			}
		}
	}

	public void setPosition(Location position) {
		loadPosition();
		giveInstructions();
	}

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

	protected void giveInstructions(){
		if(!enoughAccuraccy){
			this.message = "No se puede obtener su posición con exactitud, por favor encienda el g p s, en caso de tenerlo encendido ya por favor dirigase a un lugar abierto";
			context.speak(this.message);
		}else{
			giveAccurateInstructions();
		}
	}

	protected abstract void giveAccurateInstructions();
}
