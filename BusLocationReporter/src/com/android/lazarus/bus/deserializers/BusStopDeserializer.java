package com.android.lazarus.bus.deserializers;

import java.lang.reflect.Type;

import com.android.lazarus.bus.model.BusStop;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class BusStopDeserializer implements JsonDeserializer<BusStop> {

	@Override
	public BusStop deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		JsonObject stop = arg0.getAsJsonObject();
		long ordinal = stop.get("ordinal").getAsLong();
		double latitude = stop.get("latitude").getAsDouble();
		double longitude = stop.get("longitude").getAsDouble();
		return new BusStop(latitude, longitude, ordinal);
	}

}
