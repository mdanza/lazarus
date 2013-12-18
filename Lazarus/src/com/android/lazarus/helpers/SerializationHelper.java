package com.android.lazarus.helpers;

import com.android.lazarus.model.Point;
import com.android.lazarus.model.serialization.PointSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationHelper {
	public static Gson gson = createGson();

	private static Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		PointSerializer pointSerializer = new PointSerializer();
		builder.registerTypeAdapter(Point.class, pointSerializer);
		return builder.create();
	}
}
