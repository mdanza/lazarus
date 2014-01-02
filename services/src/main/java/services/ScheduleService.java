package services;

import helpers.RestResultsHelper;

import java.lang.reflect.Type;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import model.Bus;
import services.authentication.AuthenticationService;
import services.directions.bus.BusExclusionStrategy;
import services.directions.bus.schedules.BusSchedulesService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Stateless(name = "ScheduleService")
@Path("v1/api/schedule")
public class ScheduleService {

	private Gson gson = createGson();

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new BusExclusionStrategy());
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
			@QueryParam("busStopLocationCode") Long busStopLocationCode,
			@QueryParam("minutesSinceStartOfDay") Integer minutesSinceStartOfDay) {
		if (token == null || token == "" || lineName == null || lineName == ""
				|| subLineDescription == null || subLineDescription == ""
				|| busStopLocationCode == null
				|| minutesSinceStartOfDay == null)
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
		} catch (Exception e) {
			e.printStackTrace();
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
		List<String> results = busSchedulesService
				.getBusLineSchedule(lineName, subLineDescription,
						busStopLocationCode, minutesSinceStartOfDay);
		if (results != null) {
			Type type = new TypeToken<List<String>>() {
			}.getType();
			return restResultsHelper.resultWrapper(true,
					gson.toJson(results, type));
		} else
			return restResultsHelper.resultWrapper(false, "No data available");
	}

	@GET
	@Path("/bus")
	public String getClosestBus(@HeaderParam("Authorization") String token,
			@QueryParam("variantCode") Long variantCode,
			@QueryParam("subLineCode") Long subLineCode,
			@QueryParam("busStopOrdinal") Long busStopOrdinal) {
		if (token == null || token == "" || variantCode == null
				|| subLineCode == null || busStopOrdinal == null)
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
		} catch (Exception e) {
			e.printStackTrace();
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
		Bus result = busSchedulesService.getClosestBus(variantCode,
				subLineCode, busStopOrdinal);
		if (result != null)
			return restResultsHelper.resultWrapper(true, gson.toJson(result));
		else
			return restResultsHelper.resultWrapper(false, "No data available");

	}
}
