package com.lazarus.busclient.deserializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lazarus.busclient.model.BusStop;

public class BusStopDeserializer implements JsonDeserializer<BusStop> {

	@Override
	public BusStop deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		JsonObject stop = arg0.getAsJsonObject();
		int ordinal = stop.get("ordinal").getAsInt();
		JsonObject coords = stop.get("point").getAsJsonObject()
				.get("coordinates").getAsJsonObject().get("coordinates")
				.getAsJsonArray().get(0).getAsJsonObject();
		double latitude = coords.get("y").getAsDouble();
		double longitude = coords.get("x").getAsDouble();
		return new BusStop(latitude, longitude, ordinal);
	}

}
