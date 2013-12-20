package services;

import helpers.RestResultsHelper;

import java.lang.reflect.Type;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import model.TaxiService;
import model.User;
import model.User.Role;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;
import services.taxis.TaxiServiceService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Stateless(name = "TaxiServiceService")
@Path("v1/api/taxis")
public class TaxiServices {

	static Logger logger = Logger.getLogger(TaxiServices.class);

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "TaxiServiceService")
	private TaxiServiceService taxiServiceService;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	private Gson gson = new Gson();

	@GET
	public String getAllTaxis(@HeaderParam("Authorization") String token) {
		try {
			authenticationService.authenticate(token);
			List<TaxiService> result = taxiServiceService.getAllTaxiService();
			if (result != null) {
				Type type = new TypeToken<List<TaxiService>>() {
				}.getType();
				return restResultsHelper.resultWrapper(true,
						gson.toJson(result, type));
			} else
				return restResultsHelper.resultWrapper(false,
						"No taxi services registered");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@POST
	public String addTaxi(@HeaderParam("Authorization") String token,
			@FormParam("name") String name, @FormParam("phone") String phone) {
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			if (name == null || name.equals("") || phone == null
					|| phone.equals(""))
				return restResultsHelper.resultWrapper(false,
						"Null or empty params");
			TaxiService tService = new TaxiService();
			tService.setName(name);
			tService.setPhone(phone);
			try {
				taxiServiceService.addTaxiService(tService);
				return restResultsHelper.resultWrapper(true,
						"Added successfully");
			} catch (Exception e) {
				return restResultsHelper.resultWrapper(false,
						"Could not add taxi service");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@DELETE
	@Path("/{name}")
	public String removeTaxi(@HeaderParam("Authorization") String token,
			@PathParam("name") String name) {
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			if (name == null || name.equals(""))
				return restResultsHelper.resultWrapper(false,
						"Null or empty params");
			try {
				taxiServiceService.removeTaxiService(taxiServiceService
						.getTaxiServiceByName(name));
				return restResultsHelper.resultWrapper(true,
						"removed successfully");
			} catch (Exception e) {
				return restResultsHelper.resultWrapper(false,
						"Could not remove taxi service");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}
}
