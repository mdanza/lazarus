package services;

import helpers.RestResultsHelper;

import java.lang.reflect.Type;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import model.TaxiService;

import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;
import services.taxis.TaxiServiceService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Stateless(name = "TaxiServices")
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
}
