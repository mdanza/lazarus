package com.android.lazarus.serviceadapter;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import com.android.lazarus.model.Obstacle;

public interface ObstacleReportingServiceAdapter {
	public boolean reportObstacle(String token, String coordinates,
			String radius, String description);

	public boolean deactivateObstacle(String token, long id);

	public List<Obstacle> getObstaclesForRoute(ArrayList<GeoPoint> route,
			String token);
}
