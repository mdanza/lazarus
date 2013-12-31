package serialization;

import java.lang.reflect.Type;

import model.Obstacle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ObstacleSerializer implements JsonSerializer<Obstacle>{

	@Override
	public JsonElement serialize(Obstacle obsctacle, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("id", obsctacle.getId());
		json.addProperty("latitude", obsctacle.getCentre().getX());
		json.addProperty("longitude", obsctacle.getCentre().getY());
		json.addProperty("description", obsctacle.getDescription());
		json.addProperty("radius", obsctacle.getRadius());
		return json;
	}

}
