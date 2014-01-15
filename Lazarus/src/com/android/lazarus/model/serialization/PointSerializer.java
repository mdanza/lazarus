package com.android.lazarus.model.serialization;

import java.lang.reflect.Type;

import com.android.lazarus.model.Point;
import com.google.gson.JsonArray;
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
		if (json.get("x") != null && json.get("y") != null) {
			point.setLatitude(json.get("x").getAsDouble());
			point.setLongitude(json.get("y").getAsDouble());
		} else {
			if (json.get("coordinates") != null) {
				JsonObject c = json.get("coordinates").getAsJsonObject();
				JsonArray coordinates = c.getAsJsonArray("coordinates");
				JsonObject coordinate = coordinates.get(0).getAsJsonObject();
				point.setLatitude(coordinate.get("x").getAsDouble());
				point.setLongitude(coordinate.get("y").getAsDouble());
			}
		}
		return point;
	}

}
