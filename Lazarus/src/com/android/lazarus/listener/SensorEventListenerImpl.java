package com.android.lazarus.listener;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorEventListenerImpl implements SensorEventListener {

	private Float azimut;
	private SensorManager mSensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private float[] mGravity;
	private float[] mGeomagnetic;
	private static final String[] DIRECTIONS = { "S", "SW", "W", "NW", "N", "NE", "E", "SE", "S" };

	public SensorEventListenerImpl(Activity context){
		super();
		init(context);
	}
	
	public String getPointingDirection() {
		if (azimut != null) {
			float newAzimut = azimut * 360 / (2 * 3.14159f);
			return headingToString2(newAzimut + 180);
		} else {
			return null;
		}
	}

	public static String headingToString2(double x) {
		return DIRECTIONS[(int) Math.round((((double) x % 360) / 45))];
	}

	protected void init(Activity context) {
		mSensorManager = (SensorManager) context
				.getSystemService(Activity.SENSOR_SERVICE);
		accelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		resume();
	}

	public void resume() {
		mSensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, magnetometer,
				SensorManager.SENSOR_DELAY_UI);
	}

	public void pause() {
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
					mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimut = orientation[0];
			}
		}
	}
}