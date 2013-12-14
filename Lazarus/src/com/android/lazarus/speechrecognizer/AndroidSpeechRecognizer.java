package com.android.lazarus.speechrecognizer;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

public class AndroidSpeechRecognizer implements
		SpeechRecognizerInterface {
	
	SpeechRecognizer speechRecognizer;

	public AndroidSpeechRecognizer(
			Activity activity, RecognitionListener recognitionListener) {
		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
		speechRecognizer.setRecognitionListener(recognitionListener);
	}

	@Override
	public void destroy() {
		speechRecognizer.destroy();
	}

	@Override
	public void startListening(Intent recognizerIntent) {
		speechRecognizer.startListening(recognizerIntent);
		
	}

	@Override
	public void stopListening() {
		speechRecognizer.stopListening();
	}

	@Override
	public void setRecognitionListener(RecognitionListener recognitionListener) {
		speechRecognizer.setRecognitionListener(recognitionListener);
	}



}
