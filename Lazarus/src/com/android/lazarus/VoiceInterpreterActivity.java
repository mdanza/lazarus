package com.android.lazarus;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.lazarus.helpers.ConstantsHelper;
import com.android.lazarus.helpers.MessageHelper;
import com.android.lazarus.helpers.MessageSplitter;
import com.android.lazarus.listener.LocationListenerImpl;
import com.android.lazarus.listener.MockLocationListener;
import com.android.lazarus.listener.RecognitionListenerImpl;
import com.android.lazarus.listener.SensorEventListenerImpl;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.UserServiceAdapterImpl;
import com.android.lazarus.speechrecognizer.AndroidSpeechRecognizer;
import com.android.lazarus.speechrecognizer.SpeechRecognizerInterface;
import com.android.lazarus.state.LogInState;
import com.android.lazarus.state.MainMenuState;
import com.android.lazarus.state.State;
import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.MapActivity;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.MapView.MapViewEventListener;
import com.mapquest.android.maps.OverlayItem;

public class VoiceInterpreterActivity extends MapActivity implements
		TextToSpeech.OnInitListener {
	public String TAG = "VoiceInterpreterActivity";
	private SpeechRecognizerInterface speechRecognizer;
	private Intent recognizerIntent;
	private SensorEventListenerImpl sensorEventListenerImpl;
	private TextToSpeech tts;
	private int MY_DATA_CHECK_CODE = 0;
	private State state;
	private LocationListenerImpl locationListener;
	private RecognitionListener recognitionListener = new RecognitionListenerImpl(
			this);
	private String token = null;
	private String initialMessage = "Bienvenido, ";
	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterImpl();
	private boolean ttsInitialize;
	private Handler handler = new Handler();
	private static final int MAXIMUM_MESSAGE_LENGTH = 185;
	private final boolean testing = true;
	private MapView map;
	private DefaultItemizedOverlay itemizedOverlay;
	private ScheduledExecutorService scheduledThreadPoolExecutor = Executors
			.newScheduledThreadPool(1);

	LogInTask logInTask = new LogInTask(this);

	public MockLocationListener mockLocationListener;
	private String saidMessage = null;
	private int messageRepetitions = 0;

	public void showToast(String content) {
		handler.post(new ShowTextRunnable(content));
	}

	private class ShowTextRunnable implements Runnable {
		private String content;

		public ShowTextRunnable(String content) {
			super();
			this.content = content;
		}

		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG)
					.show();
		}

	}

	public SensorEventListenerImpl getSensorEventListenerImpl() {
		return sensorEventListenerImpl;
	}

	public TextToSpeech getTts() {
		return tts;
	}

	public void setTts(TextToSpeech tts) {
		this.tts = tts;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
		this.state.onAttach();
	}

	public void setState(State state, boolean runOnAttach) {
		this.state = state;
		if (runOnAttach)
			this.state.onAttach();
	}

	public LocationListenerImpl getLocationListener() {
		return locationListener;
	}

	public void setLocationListener(LocationListenerImpl locationListener) {
		this.locationListener = locationListener;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void speak(String message) {
		if (needsHelp()) {
			message = getHelp();
		}
		int maximumLength = MAXIMUM_MESSAGE_LENGTH;
		message = MessageHelper.convertToSpeakableMessage(message);
		if (message != null && !message.equals("")) {
			tts.stop();
			if (message.length() <= maximumLength) {
				tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
			} else {
				String[] parts = MessageSplitter.splitMessage(message,
						maximumLength, "\\.\\.");
				for (String part : parts) {
					tts.speak(part, TextToSpeech.QUEUE_ADD, null);
				}
			}
		}
	}

	public void speak(String message, boolean addQueue) {
		if (needsHelp()) {
			message = getHelp();
		}
		message = MessageHelper.convertToSpeakableMessage(message);
		if (message != null) {
			int maximumLength = MAXIMUM_MESSAGE_LENGTH;
			if (addQueue == true) {
				if (message != null) {
					if (message.length() <= maximumLength) {
						tts.speak(message, TextToSpeech.QUEUE_ADD, null);
					} else {
						String[] parts = MessageSplitter.splitMessage(message,
								maximumLength, "\\.\\.");
						for (String part : parts) {
							tts.speak(part, TextToSpeech.QUEUE_ADD, null);
						}
					}
				}
			} else {
				speak(message);
			}
		}
	}

	private String getHelp() {
		String newMessage = null;
		if (state != null) {
			String message = state.getMessage();
			if (message != null) {
				newMessage = this.state.getHelpMessage() + " " + message;
			} else {
				newMessage = this.state.getHelpMessage();
			}
		}
		return newMessage;
	}

	private boolean needsHelp() {
		boolean needsHelp = false;
		if (state != null) {
			String message = state.getMessage();
			if (saidMessage == null) {
				saidMessage = message;
			} else {
				if ((message != null && !message.equals("") && message
						.equals(saidMessage))) {
					messageRepetitions++;
					if (messageRepetitions == 3) {
						message = null;
						messageRepetitions = 0;
						needsHelp = true;
					}
				}
			}
		}
		return needsHelp;
	}

	public void sayMessage() {
		speak(this.state.getMessage());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		speechRecognizer = new AndroidSpeechRecognizer(this,
				recognitionListener);
		recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-AR");
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				"voice.recognition.test");
		// recognizerIntent
		// .putExtra(
		// RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
		// 100000);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

		if (!testing)
			setContentView(R.layout.activity_voice_interpreter);
		else
			setContentView(R.layout.activity_voice_interpreter_testing);

		sensorEventListenerImpl = new SensorEventListenerImpl(this);

		Button pushToTalkBtn = (Button) findViewById(R.id.pushToTalkButton);
		pushToTalkBtn.setOnTouchListener(pushToTalkListener);
		pushToTalkBtn.setSoundEffectsEnabled(false);

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		if (!testing)
			locationListener = new LocationListenerImpl(this);
		else {
			mockLocationListener = new MockLocationListener(this);
			locationListener = mockLocationListener;
			setUpMap();
		}
		initializeFirstState();
		scheduledThreadPoolExecutor.scheduleAtFixedRate(
				new Runnable() {

					@Override
					public void run() {
						String username = VoiceInterpreterActivity.this
								.getSharedPreferences("usrpref", 0).getString(
										"username", null);
						String password = VoiceInterpreterActivity.this
								.getSharedPreferences("usrpref", 0).getString(
										"password", null);
						String result = null;
						if (username != null && password != null)
							result = userServiceAdapter.login(username,
									password);
						if (result != null)
							token = result;
						else
							VoiceInterpreterActivity.this
									.setState(new LogInState(
											VoiceInterpreterActivity.this,
											initialMessage));

					}
				}, ConstantsHelper.REFRESH_TOKEN_RATE,
				ConstantsHelper.REFRESH_TOKEN_RATE, TimeUnit.MINUTES);
	}

	private void setUpMap() {
		map = (MapView) findViewById(R.id.map);
		map.getController().setZoom(16);
		map.getController().setCenter(new GeoPoint(-34.900557, -56.140355));
		map.setBuiltInZoomControls(true);
		map.addMapViewEventListener(new MapViewEventListener() {

			@Override
			public void longTouch(MapView arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mapLoaded(MapView arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void move(MapView map) {
				GeoPoint center = map.getMapCenter();
				Location location = new Location("");
				location.setLatitude(center.getLatitude());
				location.setLongitude(center.getLongitude());
				location.setAccuracy(19);
				location.setAltitude(0);
				location.setTime(System.currentTimeMillis());
				location.setBearing(0F);
				locationListener.onLocationChanged(location);
				if (itemizedOverlay != null)
					map.getOverlays().remove(itemizedOverlay);
				Drawable icon = getResources().getDrawable(
						R.drawable.location_marker);
				itemizedOverlay = new DefaultItemizedOverlay(icon);
				OverlayItem myPosition = new OverlayItem(center, "", "");
				itemizedOverlay.addItem(myPosition);
				map.getOverlays().add(itemizedOverlay);
				map.invalidate();
			}

			@Override
			public void moveEnd(MapView arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void moveStart(MapView arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void touch(MapView arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void zoomEnd(MapView arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void zoomStart(MapView arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void initializeFirstState() {
		String username = this.getSharedPreferences("usrpref", 0).getString(
				"username", null);
		String password = this.getSharedPreferences("usrpref", 0).getString(
				"password", null);
		if (username != null && password != null) {
			String[] args = new String[2];
			args[0] = username;
			args[1] = password;
			if (logInTask.getStatus() != AsyncTask.Status.RUNNING) {
				if (logInTask.getStatus() == AsyncTask.Status.PENDING) {
					logInTask.execute(args);
				} else {
					if (logInTask.getStatus() == AsyncTask.Status.FINISHED) {
						logInTask = new LogInTask(this);
						logInTask.execute(args);
					}
				}
			}
		} else {
			LogInState logInState = new LogInState(this, initialMessage);
			this.setState(logInState);
		}
	}

	View.OnTouchListener pushToTalkListener = new View.OnTouchListener() {
		public boolean onTouch(View view, MotionEvent motionEvent) {

			if (tts != null && speechRecognizer != null) {
				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					speechRecognizer.startListening(recognizerIntent);
					tts.stop();
					return true;
				case MotionEvent.ACTION_UP:
					tts.speak(
							". Espere mientras procesamos el resultado por favor",
							TextToSpeech.QUEUE_FLUSH, null);
					speechRecognizer.stopListening();
					return true;
				case MotionEvent.ACTION_MOVE:
					return true;
				}
			}

			return true;
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// the user has the necessary data - create the TTS
				tts = new TextToSpeech(this, this);
			} else {
				// no data - install it now
				Intent installTTSIntent = new Intent();
				installTTSIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installTTSIntent);
			}
		}
	}

	@Override
	public void onInit(int initStatus) {
		// check for successful instantiation
		if (initStatus == TextToSpeech.SUCCESS) {
			ttsInitialize = true;
			Locale loc = new Locale("spa", "ARG");
			if (tts.isLanguageAvailable(loc) != TextToSpeech.LANG_NOT_SUPPORTED
					&& tts.isLanguageAvailable(loc) != TextToSpeech.LANG_MISSING_DATA)
				tts.setLanguage(loc);
			if (state != null) {
				speak(state.getMessage());
			}
		} else if (initStatus == TextToSpeech.ERROR) {
			Toast.makeText(this, "Sorry! Text To Speech failed...",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorEventListenerImpl.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorEventListenerImpl.resume();
		if(state instanceof MainMenuState)
			sayMessage();
	}

	@Override
	public void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		sensorEventListenerImpl.pause();
		speechRecognizer.destroy();
		super.onDestroy();
	}

	private class LogInTask extends AsyncTask<String, Void, Void> {

		VoiceInterpreterActivity voiceInterpreterActivity;

		public LogInTask(VoiceInterpreterActivity context) {
			super();
			this.voiceInterpreterActivity = context;
		}

		@Override
		protected Void doInBackground(String... args) {
			String result = userServiceAdapter.login(args[0], args[1]);
			if (result != null) {
				token = result;
				MainMenuState mainMenuState = new MainMenuState(
						voiceInterpreterActivity, initialMessage);
				voiceInterpreterActivity.setState(mainMenuState);
			} else {
				voiceInterpreterActivity.getSharedPreferences("usrpref", 0)
						.edit().clear().commit();
				LogInState logInState = new LogInState(
						voiceInterpreterActivity, initialMessage);
				voiceInterpreterActivity.setState(logInState);
			}
			if (ttsInitialize)
				speak(state.getMessage());
			return null;
		}
	}

	public void makeCall(String number) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + number));
			startActivity(callIntent);
			System.exit(0);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "Could not make phone call");
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}