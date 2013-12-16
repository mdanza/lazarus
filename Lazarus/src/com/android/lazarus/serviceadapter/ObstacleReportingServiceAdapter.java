package com.android.lazarus.serviceadapter;

public interface ObstacleReportingServiceAdapter {
	public boolean reportObstacle(String token, String coordinates,
			String radius, String description);

	public boolean deactivateObstacle(String token, String coordinates);
}
