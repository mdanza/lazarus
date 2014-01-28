package com.android.lazarus.helpers;

public class MessageTransformer {

	/**
	 * "5.1" to "5 coma un" "5.2" to "5 coma dos"
	 * 
	 * @return
	 */
	public static String convertToSpeakableMessage(String message) {
		String speakableMessage = null;
		if (message != null) {
			String[] parts = message.split("\\ ");
			for (int i = 0; i < parts.length; i++) {
				try {
					double number = Double.valueOf(parts[i]);
					
				} catch (NumberFormatException e) {
					
				}
			}
		}
		return speakableMessage;
	}
}
