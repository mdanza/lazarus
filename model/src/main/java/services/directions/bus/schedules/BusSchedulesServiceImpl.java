package services.directions.bus.schedules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Bus;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Stateless(name = "BusSchedulesService")
public class BusSchedulesServiceImpl implements BusSchedulesService {
	@PersistenceContext(unitName = "lazarus-persistence-unit")
	private EntityManager entityManager;

	JsonParser jsonParser = new JsonParser();

	public Bus getClosestBus(int variantCode, int subLineCode,
			int maximumBusStopOrdinal) {
		Query q = entityManager
				.createQuery("SELECT b FROM Bus b WHERE b.variantCode = :variantCode AND b.subLineCode = :subLineCode AND b.lastPassedStopOrdinal < :maximumOrdinal ORDER BY b.lastPassedStopOrdinal DESC");
		q.setParameter("variantCode", variantCode);
		q.setParameter("subLineCode", subLineCode);
		q.setParameter("maximumOrdinal", maximumBusStopOrdinal);
		q.setMaxResults(1);
		try {
			return (Bus) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<String> getBusLineSchedule(String lineName,
			String subLineDescription, int busStopLocationCode,
			int fromMinutesSinceStartOfDay) {
		try {
			String typeOfDay = getTypeOfDay();
			String lineCode = getLineCode(lineName, busStopLocationCode);
			String time = fromMinutesSinceStartOfDay / 60 + ":"
					+ fromMinutesSinceStartOfDay % 60;
			String url = "http://www.montevideo.gub.uy/transporteRestProd/pasadas/"
					+ busStopLocationCode
					+ "/"
					+ typeOfDay
					+ "/"
					+ lineCode
					+ "/" + time;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				List<String> result = new ArrayList<String>();
				JsonArray json = jsonParser.parse(response.toString())
						.getAsJsonArray();
				String bestMatchDescription = getBestMatchDescription(json,
						subLineDescription);
				for (JsonElement el : json) {
					JsonObject timeObject = el.getAsJsonObject();
					if (timeObject.get("destino").getAsString()
							.equalsIgnoreCase(bestMatchDescription))
						result.add(timeObject.get("horaDesc").getAsString());
				}
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getBestMatchDescription(JsonArray json,
			String subLineDescription) {
		String subLineTrimmed = StringUtils.deleteWhitespace(
				StringUtils.remove(StringUtils.remove(StringUtils.remove(
						StringUtils.remove(
								StringUtils.remove(subLineDescription, '\\'),
								'/'), '-'), ')'), '(')).toUpperCase();
		int count = 0;
		int bestMatchDistance = -1;
		int bestMatchIndex = -1;
		String bestMatchDescription = "";
		for (JsonElement el : json) {
			JsonObject obj = el.getAsJsonObject();
			String description = obj.get("destino").getAsString();
			String descriptionTrimmed = StringUtils.deleteWhitespace(
					StringUtils.remove(
							StringUtils.remove(StringUtils.remove(StringUtils
									.remove(StringUtils.remove(description,
											'\\'), '/'), '-'), ')'), '('))
					.toUpperCase();
			int distance = StringUtils.getLevenshteinDistance(subLineTrimmed,
					descriptionTrimmed);
			if (bestMatchDistance == -1 || distance < bestMatchDistance) {
				bestMatchDistance = distance;
				bestMatchIndex = count;
				bestMatchDescription = description;
			}
			count++;
		}
		if (bestMatchIndex != -1)
			return bestMatchDescription;
		return null;
	}

	private String getTypeOfDay() throws IOException {
		String url = "http://www.montevideo.gub.uy/transporteRestProd/hora/";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JsonObject json = jsonParser.parse(response.toString())
					.getAsJsonObject();
			return json.get("tipoDia").getAsString();
		}
		return null;
	}

	private String getLineCode(String lineName, int busStopLocationCode)
			throws IOException {
		String url = "http://www.montevideo.gub.uy/transporteRestProd/lineas/"
				+ busStopLocationCode;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JsonObject json = jsonParser.parse(response.toString())
					.getAsJsonObject();
			JsonArray lines = json.getAsJsonArray("lineas");
			for (JsonElement lineElement : lines) {
				JsonObject line = lineElement.getAsJsonObject();
				if (line.get("descripcion").getAsString()
						.equalsIgnoreCase(lineName))
					return line.get("codigo").getAsString();
			}
		}
		return null;
	}
}
