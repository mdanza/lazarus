package com.android.lazarus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class VoiceInterpreterActivity extends FragmentActivity implements
		TextToSpeech.OnInitListener {

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
	public MockLocationListener mockLocationListener = new MockLocationListener(this);

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
		int maximumLength = 245;
		tts.stop();
		if (message != null) {
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
		if (message != null) {
			int maximumLength = 245;
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

	public void sayMessage() {
		speak(this.state.getMessage());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_voice_interpreter);
		sensorEventListenerImpl = new SensorEventListenerImpl(this);

		speechRecognizer = new AndroidSpeechRecognizer(this,
				recognitionListener);
		recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
//				"es-ES");
//		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
//				"voice.recognition.test");
//		recognizerIntent
//				.putExtra(
//						RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
//						100000);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

		Button pushToTalkBtn = (Button) findViewById(R.id.pushToTalkButton);
		pushToTalkBtn.setOnTouchListener(pushToTalkListener);
		pushToTalkBtn.setSoundEffectsEnabled(false);

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

		locationListener = new LocationListenerImpl(this);
		initializeFirstState();

	}

	private void initializeFirstState() {
		String username = this.getSharedPreferences("usrpref", 0).getString(
				"username", null);
		String password = this.getSharedPreferences("usrpref", 0).getString(
				"password", null);
		if (username != null && password != null) {
			LogInTask logInTask = new LogInTask(this);
			String[] args = new String[2];
			args[0] = username;
			args[1] = password;
			logInTask.execute(args);
		} else {
			LogInState logInState = new LogInState(this, initialMessage);
			this.setState(logInState);
		}
	}

	View.OnTouchListener pushToTalkListener = new View.OnTouchListener() {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				speechRecognizer.startListening(recognizerIntent);
				tts.stop();
				return true;
			case MotionEvent.ACTION_UP:
				speechRecognizer.stopListening();
				tts.speak(
						". Espere mientras procesamos el resultado por favor",
						TextToSpeech.QUEUE_FLUSH, null);
				return true;
			case MotionEvent.ACTION_MOVE:
				return true;
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
			// if (tts.isLanguageAvailable(Locale.US) ==
			// TextToSpeech.LANG_AVAILABLE)
			// tts.setLanguage(Locale.US);
			// float rate = Float.parseFloat("1");
			// tts.setSpeechRate(rate);
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

	private class LogInTask extends AsyncTask<String, Void, String> {

		VoiceInterpreterActivity voiceInterpreterActivity;

		public LogInTask(VoiceInterpreterActivity context) {
			super();
			this.voiceInterpreterActivity = context;
		}

		@Override
		protected String doInBackground(String... args) {
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
			return result;
		}
	}

}