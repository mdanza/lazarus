package com.android.lazarus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.lazarus.listener.LocationListenerImpl;
import com.android.lazarus.listener.RecognitionListenerImpl;
import com.android.lazarus.serviceadapter.UserServiceAdapter;
import com.android.lazarus.serviceadapter.stubs.UserServiceAdapterStub;
import com.android.lazarus.sharedpreference.ObscuredSharedPreferences;
import com.android.lazarus.state.LogInState;
import com.android.lazarus.state.MainMenuState;
import com.android.lazarus.state.State;

public class VoiceInterpreterActivity extends Activity implements
		TextToSpeech.OnInitListener {

	SpeechRecognizer speechRecognizer;
	String stringResults = new String();
	Intent recognizerIntent;
	private TextToSpeech tts;
	private int MY_DATA_CHECK_CODE = 0;
	private State state;
	private LocationListenerImpl locationListener;
	private RecognitionListener recognitionListener = new RecognitionListenerImpl(
			this);
	private SharedPreferences preferences = null;
	private UserServiceAdapter userServiceAdapter = new UserServiceAdapterStub();

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.activity_voice_interpreter);
		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		speechRecognizer.setRecognitionListener(recognitionListener);
		recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
				"es-ES");
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				"voice.recognition.test");
		recognizerIntent
				.putExtra(
						RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
						100000);

		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

		findViewById(R.id.pushToTalkButton).setOnTouchListener(
				pushToTalkListener);

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

		preferences = new ObscuredSharedPreferences(this,
				this.getSharedPreferences("usrpref", Context.MODE_PRIVATE));
		boolean validDataStored = false;
		String initialMessage = "Bienvenido a  Lázarus, ";
		if (this.getSharedPreferences("usrpref", 0).getString("username", null) != null
				&& this.getSharedPreferences("usrpref", 0).getString("password", null) != null) {
			if (userServiceAdapter.login(
					this.getSharedPreferences("usrpref", 0).getString("username", null),
					this.getSharedPreferences("usrpref", 0).getString("password", null)) == true) {
				validDataStored = true;
			}
		}
		if (validDataStored) {
			MainMenuState mainMenuState = new MainMenuState(this,
					initialMessage);
			this.setState(mainMenuState);
		} else {
			state = new LogInState(this, initialMessage);
		}
		 locationListener = new LocationListenerImpl(this);

	}

	View.OnTouchListener pushToTalkListener = new View.OnTouchListener() {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				speechRecognizer.startListening(recognizerIntent);
				tts.stop();
				break;
			case MotionEvent.ACTION_UP:
				speechRecognizer.stopListening();
				tts.speak(". Espere mientras procesamos el resultado por favor",
						TextToSpeech.QUEUE_FLUSH, null);
				break;
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
			// if (tts.isLanguageAvailable(Locale.US) ==
			// TextToSpeech.LANG_AVAILABLE)
			// tts.setLanguage(Locale.US);
			tts.speak(state.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
		} else if (initStatus == TextToSpeech.ERROR) {
			Toast.makeText(this, "Sorry! Text To Speech failed...",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		speechRecognizer.destroy();
		super.onDestroy();
	}
	
	
}
