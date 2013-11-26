package com.android.lazarus;

import android.app.Activity;
import android.content.Intent;
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
import com.android.lazarus.state.LogInState;
import com.android.lazarus.state.State;

public class VoiceInterpreterActivity extends Activity implements
		TextToSpeech.OnInitListener {

	SpeechRecognizer speechRecognizer;
	String stringResults = new String();
	Intent recognizerIntent;
	private TextToSpeech tts;
	private int MY_DATA_CHECK_CODE = 0;
	private State state;
	private LocationListener locationListener = new LocationListenerImpl();
	private RecognitionListener recognitionListener = new RecognitionListenerImpl(this);
	
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

		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

		findViewById(R.id.pushToTalkButton).setOnTouchListener(
				pushToTalkListener);

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		
		
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000,
		            10, locationListener);

		state = new LogInState(this);

	}

	View.OnTouchListener pushToTalkListener = new View.OnTouchListener() {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				speechRecognizer.startListening(recognizerIntent);
				break;
			case MotionEvent.ACTION_UP:
				speechRecognizer.stopListening();
				tts.speak("Espere mientras procesamos el resultado",
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
