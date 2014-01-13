package com.android.lazarus.model.serialization;

import java.lang.reflect.Type;

import org.osmdroid.util.GeoPoint;

import com.android.lazarus.model.Point;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GeoPointSerializer implements JsonSerializer<GeoPoint> {

	@Override
	public JsonElement serialize(GeoPoint geoPoint, Type arg1,
			JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("x", geoPoint.getLatitude());
		json.addProperty("y", geoPoint.getLongitude());
		
		return json;
	}


}
