package com.android.lazarus;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class VoiceInterpreterActivity extends Activity implements
		TextToSpeech.OnInitListener {

	SpeechRecognizer speechRecognizer;
	String stringResults = new String();
	Intent recognizerIntent;
	private TextToSpeech tts;
	private int MY_DATA_CHECK_CODE = 0;

	RecognitionListener recognitionListener = new RecognitionListener() {

		@Override
		public void onRmsChanged(float rmsdB) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResults(Bundle results) {

			ArrayList data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			for (int i = 0; i < data.size(); i++) {
				stringResults += data.get(i);
			}

			Context context = getApplicationContext();
			CharSequence text = stringResults;
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();

		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			Context context = getApplicationContext();
			CharSequence text = stringResults;
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, "Pronto", duration);
			toast.show();

		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(int error) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEndOfSpeech() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBeginningOfSpeech() {
			// TODO Auto-generated method stub

		}
	};

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

		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

		findViewById(R.id.pushToTalkButton).setOnTouchListener(
				pushToTalkListener);

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

	}

	View.OnTouchListener pushToTalkListener = new View.OnTouchListener() {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:

				speechRecognizer.startListening(recognizerIntent);

				break;
			case MotionEvent.ACTION_UP:
				speechRecognizer.stopListening();
				stringResults = "";
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
			//if (tts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
				//tts.setLanguage(Locale.US);
			tts.speak("Hola, ¿Qué tal?", TextToSpeech.QUEUE_FLUSH, null);
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
