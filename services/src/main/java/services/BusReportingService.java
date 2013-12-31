package services;

import helpers.RestResultsHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import model.Bus;
import model.BusStop;
import model.ShapefileWKT;
import model.dao.BusDAO;
import model.dao.BusStopDAO;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.authentication.AuthenticationService;
import services.directions.walking.WalkingPositionExclusionStrategy;
import services.shapefiles.utils.CoordinateConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Stateless(name = "BusReportingService")
@Path("v1/api/bus")
public class BusReportingService {

	private Gson gson = createGson();

	@EJB(name = "BusDAO")
	private BusDAO busDAO;

	@EJB(name = "BusStopDAO")
	private BusStopDAO busStopDAO;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new WalkingPositionExclusionStrategy());
		return builder.create();
	}

	@POST
	public String registerBus(@HeaderParam("Authorization") String token,
			@FormParam("variantCode") Long variantCode,
			@FormParam("subLineCode") Long subLineCode,
			@FormParam("longitude") Double latitude,
			@FormParam("latitude") Double longitude) {
		if (token == null || token == "" || variantCode == null
				|| subLineCode == null || latitude == null || longitude == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Bus result = new Bus();
		result.setLastPassedStopOrdinal(Long.MAX_VALUE);
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
			@FormParam("variantCode") Long variantCode,
			@FormParam("subLineCode") Long subLineCode,
			@FormParam("longitude") Double latitude,
			@FormParam("latitude") Double longitude,
			@PathParam("busId") String busId,
			@FormParam("lastPassedStopOrdinal") Long lastPassedStopOrdinal) {
		if (token == null || token == "" || variantCode == null
				|| subLineCode == null || latitude == null || longitude == null
				|| busId == null || busId == ""
				|| lastPassedStopOrdinal == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		Long busIdLong = null;
		try {
			busIdLong = Long.parseLong(busId);
		} catch (NumberFormatException e) {
			return null;
		}
		authenticationService.authenticate(token);
		Bus result = busDAO.find(busIdLong);
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
			@PathParam("busId") Long busId) {
		if (token == null || token == "" || busId == null)
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			Bus result = busDAO.find(busId);
			if (result != null) {
				return restResultsHelper.resultWrapper(true,
						gson.toJson(result));
			} else
				return restResultsHelper.resultWrapper(false, "No bus found");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	@Path("/{busId}/stops")
	public String getBusStops(@HeaderParam("Authorization") String token,
			@PathParam("busId") Long busId) {
		if (token == null || token == "" || busId == null)
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Bus bus = busDAO.find(busId);
		if (bus == null)
			return null;
		List<BusStop> result = busStopDAO.getLineStops(bus.getVariantCode());
		if (result != null) {
			List<BusStop> stops = new ArrayList<BusStop>();
			for (BusStop stop : result) {
				try {
					BusStop copy = new BusStop(stop);
					copy.setPoint(coordinateConverter.convertToWGS84(
							copy.getPoint(), ShapefileWKT.BUS_STOP));
					stops.add(copy);
				} catch (MismatchedDimensionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FactoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return gson.toJson(stops);
		} else
			return null;
	}
}
