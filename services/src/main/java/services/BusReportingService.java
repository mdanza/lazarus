package services;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import model.Bus;
import model.BusStop;
import model.dao.BusDAO;
import model.dao.BusStopDAO;
import services.authentication.AuthenticationService;
import services.directions.walking.WalkingPositionExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Stateless(name = "BusReportingService")
@Path("/api/bus")
public class BusReportingService {

	private Gson gson = createGson();

	@EJB(name = "BusDAO")
	private BusDAO busDAO;

	@EJB(name = "BusStopDAO")
	private BusStopDAO busStopDAO;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new WalkingPositionExclusionStrategy());
		return builder.create();
	}

	@POST
	public String registerBus(@HeaderParam("Authorization") String token,
			@QueryParam("variantCode") Integer variantCode,
			@QueryParam("subLineCode") Integer subLineCode,
			@QueryParam("longitude") Double latitude,
			@QueryParam("latitude") Double longitude) {
		if (token == null || token == "" || variantCode == null
				|| subLineCode == null || latitude == null || longitude == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Bus result = new Bus();
		result.setLastPassedStopOrdinal(Integer.MAX_VALUE);
		result.setVariantCode(variantCode);
		result.setSubLineCode(subLineCode);
		result.setLatitude(latitude);
		result.setLongitude(longitude);
		result.setLastUpdated(new Date());
		busDAO.add(result);
		return gson.toJson(result);
	}

	@PUT
	@Path("/{busId}")
	public String updateBus(@HeaderParam("Authorization") String token,
			@QueryParam("variantCode") Integer variantCode,
			@QueryParam("subLineCode") Integer subLineCode,
			@QueryParam("longitude") Double latitude,
			@QueryParam("latitude") Double longitude,
			@PathParam("busId") String busId,
			@QueryParam("lastPassedStopOrdinal") Integer lastPassedStopOrdinal) {
		if (token == null || token == "" || variantCode == null
				|| subLineCode == null || latitude == null || longitude == null
				|| busId == null || busId == ""
				|| lastPassedStopOrdinal == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		Integer busIdInt = null;
		try {
			busIdInt = Integer.parseInt(busId);
		} catch (NumberFormatException e) {
			return null;
		}
		authenticationService.authenticate(token);
		Bus result = busDAO.find(busIdInt);
		if (result == null)
			return null;
		result.setLastPassedStopOrdinal(lastPassedStopOrdinal);
		result.setVariantCode(variantCode);
		result.setSubLineCode(subLineCode);
		result.setLatitude(latitude);
		result.setLongitude(longitude);
		result.setLastUpdated(new Date());
		busDAO.modify(null, result);
		return gson.toJson(result);
	}

	@GET
	@Path("/{busId}")
	public String getBus(@HeaderParam("Authorization") String token,
			@PathParam("busId") Integer busId) {
		if (token == null || token == "" || busId == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Bus result = busDAO.find(busId);
		if (result != null)
			return gson.toJson(result);
		else
			return null;
	}

	@GET
	@Path("/{busId}/stops")
	public String getBusStops(@HeaderParam("Authorization") String token,
			@PathParam("busId") Integer busId) {
		if (token == null || token == "" || busId == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Bus bus = busDAO.find(busId);
		if (bus == null)
			return null;
		List<BusStop> result = busStopDAO.getLineStops(bus.getVariantCode());
		if (result != null)
			return gson.toJson(result);
		else
			return null;
	}
}
