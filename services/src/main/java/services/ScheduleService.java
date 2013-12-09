package services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.Bus;
import services.authentication.AuthenticationService;
import services.directions.bus.schedules.BusSchedulesService;
import services.directions.walking.WalkingPositionExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Stateless(name = "ScheduleService")
@Path("v1/api/schedule")
public class ScheduleService {

	private Gson gson = createGson();

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new WalkingPositionExclusionStrategy());
		return builder.create();
	}

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "BusSchedulesService")
	private BusSchedulesService busSchedulesService;

	@GET
	public String getBusSchedule(@HeaderParam("Authorization") String token,
			@QueryParam("lineName") String lineName,
			@QueryParam("subLineDescription") String subLineDescription,
			@QueryParam("busStopLocationCode") Integer busStopLocationCode,
			@QueryParam("minutesSinceStartOfDay") Integer minutesSinceStartOfDay) {
		if (token == null || token == "" || lineName == null || lineName == ""
				|| subLineDescription == null || subLineDescription == ""
				|| busStopLocationCode == null
				|| minutesSinceStartOfDay == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		List<String> results = busSchedulesService
				.getBusLineSchedule(lineName, subLineDescription,
						busStopLocationCode, minutesSinceStartOfDay);
		return gson.toJson(results);
	}

	@GET
	@Path("/bus")
	public String getClosestBus(@HeaderParam("Authorization") String token,
			@QueryParam("variantCode") Integer variantCode,
			@QueryParam("subLineCode") Integer subLineCode,
			@QueryParam("busStopOrdinal") Integer busStopOrdinal) {
		if (token == null || token == "" || variantCode == null
				|| subLineCode == null || busStopOrdinal == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Bus result = busSchedulesService.getClosestBus(variantCode,
				subLineCode, busStopOrdinal);
		if (result != null)
			return gson.toJson(result);
		else
			return null;

	}
}
