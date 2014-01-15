package com.android.lazarus.helpers;

public class GPScoordinateHelper {

	/**
	 * Calculates the distance between two given Gps points
	 * 
	 * @param pointOne
	 *            GpsPoint one
	 * @param pointTwo
	 *            GpsPoint two
	 * @return distance in meters
	 */
	public static double getDistanceBetweenPoints(double latOne, double latTwo,
			double lngOne, double lngTwo) {
		int R = 6371000; // Earth radius in meters

		double dLat = (latTwo - latOne) * Math.PI / 180;
		double dLon = (lngTwo - lngOne) * Math.PI / 180;
		double latRadOne = latOne * Math.PI / 180;
		double latRadTwo = latTwo * Math.PI / 180;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(latRadOne)
				* Math.cos(latRadTwo);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		return d;
	}
}
