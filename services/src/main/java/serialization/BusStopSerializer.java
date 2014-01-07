package serialization;

import java.lang.reflect.Type;

import model.BusStop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BusStopSerializer implements JsonSerializer<BusStop> {

	@Override
	public JsonElement serialize(BusStop stop, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("id", stop.getId());
		json.addProperty("latitude", stop.getPoint().getY());
		json.addProperty("longitude", stop.getPoint().getX());
		json.addProperty("ordinal", stop.getOrdinal());
		json.addProperty("locationCode", stop.getBusStopLocationCode());
		json.addProperty("active", stop.isActive());
		return json;
	}

}
