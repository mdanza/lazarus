package com.android.lazarus.serviceadapter.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

	public static List<String> toLowerCase(List<String> strings) {
		List<String> newArray = new ArrayList<String>();
		for(String line:strings){
			newArray.add(line.toLowerCase());
		}
		return newArray;
	}

}
