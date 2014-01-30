package services;

import helpers.RestResultsHelper;

import java.lang.reflect.Type;
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
import javax.ws.rs.QueryParam;

import model.Bus;
import model.BusStop;
import model.ShapefileWKT;
import model.dao.BusDAO;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import serialization.BusStopSerializer;
import services.authentication.AuthenticationService;
import services.directions.bus.BusExclusionStrategy;
import services.incidents.busstops.BusStopService;
import services.shapefiles.utils.CoordinateConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Stateless(name = "BusReportingService")
@Path("v1/api/bus")
public class BusReportingService {

	private Gson gson = createGson();

	@EJB(name = "BusDAO")
	private BusDAO busDAO;

	@EJB(name = "BusStopService")
	private BusStopService busStopService;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "CoordinateConverter")
	private CoordinateConverter coordinateConverter;

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.registerTypeAdapter(BusStop.class, new BusStopSerializer());
		builder.setExclusionStrategies(new BusExclusionStrategy());
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
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			Bus result = new Bus();
			result.setLastPassedStopOrdinal(Long.MAX_VALUE);
			result.setVariantCode(variantCode);
			result.setSubLineCode(subLineCode);
			result.setLatitude(latitude);
			result.setLongitude(longitude);
			result.setLastUpdated(new Date());
			try {
				busDAO.add(result);
				return restResultsHelper.resultWrapper(true,
						gson.toJson(result));
			} catch (Exception e) {
				return restResultsHelper.resultWrapper(false,
						"Error adding bus");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
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
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		Long busIdLong = null;
		try {
			busIdLong = Long.parseLong(busId);
		} catch (NumberFormatException e) {
			return restResultsHelper.resultWrapper(false, "Malformed bus id");
		}
		try {
			authenticationService.authenticate(token);
			Bus result = busDAO.find(busIdLong);
			if (result == null)
				return restResultsHelper.resultWrapper(false,
						"Id does not match any bus");
			result.setLastPassedStopOrdinal(lastPassedStopOrdinal);
			result.setVariantCode(variantCode);
			result.setSubLineCode(subLineCode);
			result.setLatitude(latitude);
			result.setLongitude(longitude);
			result.setLastUpdated(new Date());
			try {
				busDAO.modify(null, result);
				return restResultsHelper.resultWrapper(true,
						gson.toJson(result));
			} catch (Exception e) {
				return restResultsHelper.resultWrapper(false,
						"Error updating bus");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
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
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			Bus bus = busDAO.find(busId);
			if (bus == null)
				return restResultsHelper.resultWrapper(false,
						"Id does not match any bus");
			List<BusStop> result = busStopService.getLineStops(bus
					.getVariantCode());
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
				Type type = new TypeToken<List<BusStop>>() {
				}.getType();
				return restResultsHelper.resultWrapper(true,
						gson.toJson(stops, type));
			} else
				return restResultsHelper.resultWrapper(false,
						"No stops for given bus");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	@Path("/all/all-stops")
	public String getBusStops(@HeaderParam("Authorization") String token,
			@QueryParam("page") int page) {
		if (token == null || token == "")
			return restResultsHelper
					.resultWrapper(false, "Empty or null token");
		if (page < 0)
			return restResultsHelper.resultWrapper(false,
					"Negative page number");
		try {
			authenticationService.authenticate(token);
			List<BusStop> result = busStopService
					.findAllDistinctLocationCodes(page);
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
				Type type = new TypeToken<List<BusStop>>() {
				}.getType();
				return restResultsHelper.resultWrapper(true,
						gson.toJson(stops, type));
			} else
				return restResultsHelper.resultWrapper(false, "No stops found");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@POST
	@Path("/all/{locationCode}")
	public String enableDisableBusStops(
			@HeaderParam("Authorization") String token,
			@FormParam("active") String active,
			@PathParam("locationCode") String locationCode) {
		if (token == null || token == "" || active == null || active == ""
				|| locationCode == null || locationCode == "")
			return restResultsHelper.resultWrapper(false,
					"Empty or null token or params");
		try {
			authenticationService.authenticate(token);
			try {
				boolean boolActive = Boolean.parseBoolean(active);
				long longLocationCode = Long.parseLong(locationCode);
				if (boolActive)
					busStopService.activateStops(longLocationCode);
				else
					busStopService.deactivateStops(longLocationCode);
				return restResultsHelper.resultWrapper(true,
						"Successfully completed operation");
			} catch (Exception e) {
				return restResultsHelper
						.resultWrapper(false,
								"Invalid fields; location code must be a long, active a boolean");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}
}
