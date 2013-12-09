package services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import services.address.AddressService;
import services.address.CloseLocationDataExclusionStrategy;
import services.authentication.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Coordinate;

@Stateless(name = "AddressesService")
@Path("v1/api/addresses")
public class AddressesService {

	private Gson gson = createGson();

	static Logger logger = Logger.getLogger(DirectionsService.class);

	@EJB(name = "AddressService")
	private AddressService addressService;

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
	public String addressNumberToCoordinates(@HeaderParam("Authorization") String token,
			@QueryParam("streetName") String streetName,
			@QueryParam("number") String number,
			@QueryParam("letter") String letter) {
		if (token == null || streetName == null || number == null
				|| token.equals("") || streetName.equals("")
				|| number.equals(""))
			throw new IllegalArgumentException(
					"Empty or null token, streetName or number are not allowed");
		if(letter==null)
			letter = "";
		authenticationService.authenticate(token);
		int intNumber = Integer.valueOf(number);
		return gson.toJson(addressService.parseAddressToCoordinates(streetName,
				intNumber, letter));
	}

	@GET
	@Path("/cornerToCoordinates")
	public String cornerToCoordinates(@HeaderParam("Authorization") String token,
			@QueryParam("mainStreet") String mainStreet,
			@QueryParam("cornerStreet") String cornerStreet) {
		if (token == null || mainStreet == null || cornerStreet == null
				|| token.equals("") || mainStreet.equals("")
				|| cornerStreet.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		return gson.toJson(addressService.parseAddressToCoordinates(mainStreet,
				cornerStreet));
	}

	@GET
	@Path("/possibleStreets")
	public String possibleStreets(@HeaderParam("Authorization") String token,
			@QueryParam("name") String name) {
		if (token == null || name == null || token.equals("")
				|| name.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		return gson.toJson(addressService.getPossibleStreets(name));
	}

	@GET
	@Path("/closeData")
	public String getCloseData(@HeaderParam("Authorization") String token,
			@QueryParam("position") String position) {
		if (token == null || position == null || token.equals("")
				|| position.equals(""))
			throw new IllegalArgumentException(
					"Empty or null arguments are not allowed");
		authenticationService.authenticate(token);
		Double x = Double.valueOf(position.split(",")[0]);
		Double y = Double.valueOf(position.split(",")[1]);
		Coordinate coordinate = new Coordinate(Double.valueOf(x),
				Double.valueOf(y));
		return gson.toJson(addressService.getCloseLocationData(coordinate));
	}

}