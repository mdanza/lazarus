package services.directions.walking.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.jsoup.Jsoup;

import services.directions.walking.WalkingPosition;

import com.vividsolutions.jts.geom.Coordinate;

public class GoogleServicesAdapter {

	/**
	 * Get path from startPoint to endPoint using Google directions api
	 * @param startPoint
	 * @param endPoint
	 * @return list of different routes, each one being a list of Object[] with Object[0] coordinate (Coordinate) and Object[1] instruction (String)
	 * @throws IOException
	 */
	public static List<List<WalkingPosition>> getRoutes(
			Coordinate startPoint, Coordinate endPoint) throws IOException {

		List<List<WalkingPosition>> routesReturn = new ArrayList<List<WalkingPosition>>();

		String start = startPoint.x + "," + startPoint.y;
		String finish = endPoint.x + "," + endPoint.y;
		String url = "http://maps.googleapis.com/maps/api/directions/json?language=es&mode=walking&sensor=false&units=mertics&alternatives=true&origin="
				+ start + "&destination=" + finish;
		JSONObject json = (JSONObject) JSONSerializer
				.toJSON(getGoogleRoutes(url));
		JSONArray routes = json.getJSONArray("routes");

		ListIterator iterator = routes.listIterator();
		JSONObject route;
		while (iterator.hasNext()) {
			route = (JSONObject) iterator.next();
			JSONArray legs = route.getJSONArray("legs");
			Iterator legsIterator = legs.iterator();
			JSONObject leg;
			List<WalkingPosition> routeCoordinates = new ArrayList<WalkingPosition>();
			
			while (legsIterator.hasNext()) {
				leg = (JSONObject) legsIterator.next();
				JSONArray steps = leg.getJSONArray("steps");
				Iterator stepsIterator = steps.iterator();
				JSONObject step;
				while (stepsIterator.hasNext()) {
					step = (JSONObject) stepsIterator.next();
					String htmlInstructions = step
							.getString("html_instructions");
					String instructions = Jsoup.parse(htmlInstructions).text();
					JSONObject polyline = step.getJSONObject("polyline");
					String points = (String) polyline.get("points");
					List<Coordinate> coordinates = decodePoly(points);
					if (coordinates != null) {
						for (int i = 0; i < coordinates.size(); i++) {
							if(i==0){
								WalkingPosition entry = new WalkingPosition();
								entry.setCoordinate(coordinates.get(i));
								entry.setInstruction(instructions);
								routeCoordinates.add(entry);
							}else{
								WalkingPosition entry = new WalkingPosition();
								entry.setCoordinate(coordinates.get(i));
								routeCoordinates.add(entry);
							}
						}
					}
				}

			}
			routesReturn.add(routeCoordinates);
		}

		return routesReturn;

	}

	/**
	 * Calls Google directions API
	 * 
	 * @param url
	 *            to call Google API
	 * @param finish
	 *            finish location to call Google API
	 * @return String with JSON response to call
	 * @throws IOException
	 */
	private static String getGoogleRoutes(String url) throws IOException {
		URL urlGoogleDirService = new URL(url);

		HttpURLConnection urlGoogleDirCon = (HttpURLConnection) urlGoogleDirService
				.openConnection();

		urlGoogleDirCon.setAllowUserInteraction(false);
		urlGoogleDirCon.setDoInput(true);
		urlGoogleDirCon.setDoOutput(false);
		urlGoogleDirCon.setUseCaches(true);
		urlGoogleDirCon.setRequestMethod("GET");
		urlGoogleDirCon.connect();

		InputStream p = urlGoogleDirCon.getInputStream();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(
				p, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);
		urlGoogleDirCon.disconnect();
		return responseStrBuilder.toString();

	}

	private static List<Coordinate> decodePoly(String encoded) {
		List<Coordinate> poly = new ArrayList<Coordinate>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			Coordinate p = new Coordinate((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

}
