package com.android.lazarus.helpers;

import org.osmdroid.util.GeoPoint;

import com.android.lazarus.model.Obstacle;
import com.android.lazarus.model.Point;
import com.android.lazarus.model.serialization.GeoPointSerializer;
import com.android.lazarus.model.serialization.ObstacleDeserializer;
import com.android.lazarus.model.serialization.PointSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationHelper {
	public static Gson gson = createGson();

	private static Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		PointSerializer pointSerializer = new PointSerializer();
		builder.registerTypeAdapter(Point.class, pointSerializer);
		GeoPointSerializer geoPointSerializer = new GeoPointSerializer();
		builder.registerTypeAdapter(GeoPoint.class, geoPointSerializer);
		ObstacleDeserializer obstacleDeserializer = new ObstacleDeserializer();
		builder.registerTypeAdapter(Obstacle.class, obstacleDeserializer);
		return builder.create();
	}
}
