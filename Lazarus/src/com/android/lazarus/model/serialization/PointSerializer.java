package com.android.lazarus.model.serialization;

import java.lang.reflect.Type;

import com.android.lazarus.model.Point;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PointSerializer implements JsonDeserializer<Point> {

	@Override
	public Point deserialize(JsonElement el, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject json = el.getAsJsonObject();
		Point point = new Point();
		point.setLatitude(json.get("x").getAsDouble());
		point.setLongitude(json.get("y").getAsDouble());
		return point;
	}

}
