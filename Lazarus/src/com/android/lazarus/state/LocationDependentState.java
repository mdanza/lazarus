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
	boolean gpsEnabled = true;
	boolean enoughAccuraccy = true;
	TextToSpeech tts;

	LocationDependentState(VoiceInterpreterActivity context) {
		super(context);
	}

	LocationDependentState(VoiceInterpreterActivity context,
			float minimumAccuraccy) {
		super(context);
		this.minimumAccuraccy = minimumAccuraccy;
		this.locationListener = context.getLocationListener();
		tts = this.context.getTts();
		loadPosition();
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
			gpsEnabled = false;
			this.message = "No se puede obtener su posición actual, por favor encienda el g p s, en caso de tenerlo encendido ya por favor dirigase a un lugar abierto";
			tts.speak(this.message, TextToSpeech.QUEUE_FLUSH, null);
		} else {
			if (locationListener.getLocation().getAccuracy() < minimumAccuraccy) {
				gpsEnabled = true;
				enoughAccuraccy = false;
				this.message = "No se puede obtener su posición con exactitud, por favor si está en un lugar cerrado salga";
				tts.speak(this.message, TextToSpeech.QUEUE_FLUSH, null);
			} else {
				gpsEnabled = true;
				enoughAccuraccy = true;
				this.position = locationListener.getLocation();
				giveInstructions();
			}
		}

	}

	public void setPosition(Location position) {
		this.position = position;
	}

	public boolean isGpsEnabled() {
		return gpsEnabled;
	}

	public void setGpsEnabled(boolean gpsEnabled) {
		this.gpsEnabled = gpsEnabled;
		if (locationListener.getLocation() != null && gpsEnabled) {
			if (locationListener.getLocation().getAccuracy() < minimumAccuraccy) {
				enoughAccuraccy = false;
				this.message = "No se puede obtener su posición con exactitud, por favor si está en un lugar cerrado salga";
				tts.speak(this.message, TextToSpeech.QUEUE_FLUSH, null);
			} else {
				enoughAccuraccy = true;
				this.position = locationListener.getLocation();
				giveInstructions();
			}
		}
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

	protected abstract void giveInstructions();
}
