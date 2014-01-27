package com.android.lazarus.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncoderHelper {
	
	public static String encode(String toEncode) throws UnsupportedEncodingException{
		String encoded = null;
		if(toEncode!=null){
			encoded = URLEncoder.encode(toEncode, ConstantsHelper.ENCODING);
			encoded = encoded.replace("+","%20");
		}
		return encoded;
	}

}
