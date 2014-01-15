package com.android.lazarus.model.serialization;

import java.lang.reflect.Type;

import com.android.lazarus.model.Obstacle;
import com.android.lazarus.model.Point;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ObstacleDeserializer implements JsonDeserializer<Obstacle> {

	@Override
	public Obstacle deserialize(JsonElement el, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject json = el.getAsJsonObject();
		Obstacle obstacle = new Obstacle();
		JsonElement id = json.get("id");
		JsonElement latitude = json.get("latitude");
		JsonElement longitude = json.get("longitude");
		JsonElement description = json.get("description");
		JsonElement radius = json.get("radius");
		if (id != null) {
			obstacle.setId(id.getAsLong());
		}
		if(latitude != null && longitude!=null){
			obstacle.setCentre(new Point(latitude.getAsDouble(), longitude.getAsDouble()));
		}
		if(description!=null){
			obstacle.setDescription(description.toString());
		}
		if(radius!=null){
			obstacle.setRadius(radius.getAsLong());
		}
		return obstacle;
	}



}
