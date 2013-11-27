package com.android.lazarus.listener;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import com.android.lazarus.VoiceInterpreterActivity;

public class RecognitionListenerImpl implements RecognitionListener {

	private VoiceInterpreterActivity voiceInterpreterActivity;

	private final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

	public RecognitionListenerImpl(VoiceInterpreterActivity context) {
		this.voiceInterpreterActivity = context;
	}

	@Override
	public void onRmsChanged(float rmsdB) {

	}

	@Override
	public void onResults(Bundle results) {
		voiceInterpreterActivity.getState().setResults(results
				.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
		voiceInterpreterActivity.getTts().speak(voiceInterpreterActivity.getState().getMessage(), TextToSpeech.QUEUE_FLUSH, null);

	}

	@Override
	public void onReadyForSpeech(Bundle params) {
         tg.startTone(ToneGenerator.TONE_PROP_BEEP);
	}

	@Override
	public void onPartialResults(Bundle partialResults) {

	}

	@Override
	public void onEvent(int eventType, Bundle params) {

	}

	@Override
	public void onError(int error) {
		String message = "Ha ocurrido un error, por favor repita el mensaje";
		switch (error) {
		case SpeechRecognizer.ERROR_AUDIO:
			message = "Ha ocurrido un error al grabar el audio, repita el mensaje";
			break;
		case SpeechRecognizer.ERROR_CLIENT:
			message = "Ha ocurrido un error, repita el mensaje";
			break;
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
			message = "Ha ocurrido un error de permisos, repita el mensaje";
			break;
		case SpeechRecognizer.ERROR_NETWORK:
			message = "Ha ocurrido un error en la red, repita su mensaje";
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			message = "Ha ocurrido un error en la red, repita el mensaje";
			break;
		case SpeechRecognizer.ERROR_NO_MATCH:
			message = "No se han podido reconocer palabras en su discurso, repita el mensaje";
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			message = "El servidor de reconocimiento está ocupado, por favor repita el mensaje";
			break;
		case SpeechRecognizer.ERROR_SERVER:
			message = "Ha ocurrido un error en el servidor de reconocimiento de voz, por favor repita el mensaje";
			break;
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			message = "No se ha escuchado nada, por favor repita su mensaje";
			break;
		}
		voiceInterpreterActivity.getTts().speak(message, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onEndOfSpeech() {

	}

	@Override
	public void onBufferReceived(byte[] buffer) {

	}

	@Override
	public void onBeginningOfSpeech() {

	}

}
