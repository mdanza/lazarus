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

import org.apache.log4j.Logger;

import services.address.AddressService;
import services.address.CloseLocationData;
import services.address.CloseLocationDataExclusionStrategy;
import services.authentication.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Coordinate;

@Stateless(name = "AddressesService")
@Path("v1/api/addresses")
public class AddressesService {

	private Gson gson = createGson();

	static Logger logger = Logger.getLogger(DirectionsService.class);

	@EJB(name = "AddressService")
	private AddressService addressService;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new CloseLocationDataExclusionStrategy());
		return builder.create();
	}

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@GET
	@Path("/addressNumberToCoordinates")
	public String addressNumberToCoordinates(
			@HeaderParam("Authorization") String token,
			@QueryParam("streetName") String streetName,
			@QueryParam("number") String number,
			@QueryParam("letter") String letter) {
		if (token == null || streetName == null || number == null
				|| token.equals("") || streetName.equals("")
				|| number.equals(""))
			return restResultsHelper
					.resultWrapper(false,
							"Empty or null token, streetName or number are not allowed");
		if (letter == null)
			letter = "";
		try {
			authenticationService.authenticate(token);
			long intNumber = Long.valueOf(number);
			Coordinate coord = addressService.parseAddressToCoordinates(
					streetName, intNumber, letter);
			if (coord != null)
				return restResultsHelper
						.resultWrapper(true, gson.toJson(coord));
			else
				return restResultsHelper.resultWrapper(false,
						"No address match");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	@Path("/cornerToCoordinates")
	public String cornerToCoordinates(
			@HeaderParam("Authorization") String token,
			@QueryParam("mainStreet") String mainStreet,
			@QueryParam("cornerStreet") String cornerStreet) {
		if (token == null || mainStreet == null || cornerStreet == null
				|| token.equals("") || mainStreet.equals("")
				|| cornerStreet.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			Coordinate coord = addressService.parseAddressToCoordinates(
					mainStreet, cornerStreet);
			if (coord != null)
				return restResultsHelper
						.resultWrapper(true, gson.toJson(coord));
			else
				return restResultsHelper.resultWrapper(false,
						"No address match");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	@Path("/possibleStreets")
	public String possibleStreets(@HeaderParam("Authorization") String token,
			@QueryParam("name") String name) {
		if (token == null || name == null || token.equals("")
				|| name.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			List<String> possibleStreets = addressService
					.getPossibleStreets(name);
			if (possibleStreets != null) {
				Type type = new TypeToken<List<String>>() {
				}.getType();
				return restResultsHelper.resultWrapper(true,
						gson.toJson(possibleStreets, type));
			} else
				return restResultsHelper.resultWrapper(false,
						"No matches found");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	@Path("/closeData")
	public String getCloseData(@HeaderParam("Authorization") String token,
			@QueryParam("position") String position) {
		if (token == null || position == null || token.equals("")
				|| position.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments are not allowed");
		try {
			authenticationService.authenticate(token);
			Double x = Double.valueOf(position.split(",")[0]);
			Double y = Double.valueOf(position.split(",")[1]);
			Coordinate coordinate = new Coordinate(Double.valueOf(x),
					Double.valueOf(y));
			CloseLocationData closeLocationData = addressService
					.getCloseLocationData(coordinate);
			if (closeLocationData != null)
				return restResultsHelper.resultWrapper(true,
						gson.toJson(closeLocationData));
			else
				return restResultsHelper.resultWrapper(false,
						"No close location data found");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

}