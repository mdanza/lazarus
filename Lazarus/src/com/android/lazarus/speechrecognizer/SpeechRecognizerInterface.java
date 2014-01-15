package com.android.lazarus.speechrecognizer;

import android.content.Intent;
import android.speech.RecognitionListener;

public interface SpeechRecognizerInterface {
	
	public void destroy();
	
	public void startListening(Intent recognizerIntent);
	
	public void stopListening();
	
	public void setRecognitionListener(RecognitionListener recognitionListener);
	

}
