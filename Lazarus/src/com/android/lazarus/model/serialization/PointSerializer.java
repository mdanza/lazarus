package com.android.lazarus.model.serialization;

import java.lang.reflect.Type;

import com.android.lazarus.model.Point;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PointSerializer implements JsonDeserializer<Point> {

	@Override
	public Point deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		// TODO Auto-generated method stub
		Point point = new Point();
		return point;
	}

}
