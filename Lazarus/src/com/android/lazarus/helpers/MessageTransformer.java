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
			StringBuilder stringBuilder = new StringBuilder();
			String[] parts = message.split("\\ ");
			for (int i = 0; i < parts.length; i++) {
				if (i != 0) {
					stringBuilder.append(" ");
				}
				try {
					double number = Double.valueOf(parts[i]);
					if (parts[i].split("\\.").length == 2) {
						String firstPart = transformFirstOnes(parts[i].split("\\.")[0]);
						String secondPart = transformSecondOnes(parts[i].split("\\.")[1]);
						stringBuilder.append(firstPart + " " + "coma" + " "
								+ secondPart);
					}else{
						stringBuilder.append(parts[i]);
					}
				} catch (NumberFormatException e) {
					// NaN
					stringBuilder.append(parts[i]);
				}
			}
			speakableMessage = stringBuilder.toString();
		}
		return speakableMessage;
	}

	private static String transformFirstOnes(String string) {
		String transformed = null;
		if(string!=null){
			if("1".equals(string) || "uno".equals(string)){
				transformed = "uno";
			}else{
				transformed = string;
			}
		}
		return transformed;
	}
	
	private static String transformSecondOnes(String string) {
		String transformed = null;
		if(string!=null){
			if("1".equals(string) || "uno".equals(string)){
				transformed = "un";
			}else{
				transformed = string;
			}
		}
		return transformed;
	}
}
