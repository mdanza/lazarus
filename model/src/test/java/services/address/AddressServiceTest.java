package services.address;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;

import services.taxis.TaxiServiceService;

public class AddressServiceTest {

	private AddressService addressService;
	private TaxiServiceService taxiServiceService;

	@Before
	public void configure() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.core.LocalInitialContextFactory");

		p.put("openejb.deployments.classpath.ear", "true");

		p.put("lazarus-persistence-unit", "new://Resource?type=DataSource");
		p.put("lazarus-persistence-unit.JdbcDriver", "org.postgresql.Driver");
		p.put("lazarus-persistence-unit.JdbcUrl",
				"jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.JdbcUrl",
				"jdbc:postgresql://localhost/lazarus");
		p.put("lazarus-persistence-unit.Username", "postgres");
		p.put("lazarus-persistence-unit.Password", "postgres");

		Context context = new InitialContext(p);

		addressService = (AddressService) context
				.lookup("AddressServiceLocal");

		taxiServiceService = (TaxiServiceService) context
				.lookup("TaxiServiceServiceLocal");

	
	}

	@Test
	public void testParseAddress() {
		
		//taxiServiceService.uploadInfo("C:/Users/Mateo/Downloads/vias_titulo_tipo_2/taxis_montevideo.csv");
		/*
		GsonBuilder builder = new GsonBuilder();
		builder.serializeSpecialFloatingPointValues();
		builder.setExclusionStrategies(new CloseLocationDataExclusionStrategy());
		Gson gson = builder.create();
		CloseLocationData closeLocationData = addressService.getCloseLocationData(new Coordinate(-34.901806,-56.140385));
		System.out.println(gson.toJson(closeLocationData));

		List<String> possible = addressService.getPossibleStreets("ruta nal 8 j gral");
		for(String one:possible){
			System.out.println(one);
		}
		
		System.out.println(addressService.parseAddressToCoordinates("MARCO BRUTO", 1417, ""));
		System.out.println(addressService.parseAddressToCoordinates("MARCO BRUTO", "AVENIDA GENERAL RIVERA"));
		*/
	}


}
