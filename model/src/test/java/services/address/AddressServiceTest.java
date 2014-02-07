package services.address;

import java.util.List;
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
*/
		/*
		List<String> possible = addressService.getPossibleStreets("benito blanco");
		for(String one:possible){
			System.out.println(one);
		}
		
		List<String> possible2 = addressService.getPossibleStreets("libertador");
		for(String one:possible2){
			System.out.println(one);
		}
		
		List<String> possible3 = addressService.getPossibleStreets("marco");
		for(String one:possible3){
			System.out.println(one);
		}
		List<String> possible4 = addressService.getPossibleStreets("de navio");
		for(String one:possible4){
			System.out.println(one);
		}
		
		List<String> possible5 = addressService.getPossibleStreets("teniente de navio");
		for(String one:possible5){
			System.out.println(one);
		}
		
		List<String> possible6 = addressService.getPossibleStreets("curso de agua");
		for(String one:possible6){
			System.out.println(one);
		}
		List<String> possible7 = addressService.getPossibleStreets("cierre de");
		for(String one:possible7){
			System.out.println(one);
		}
		
		List<String> possible8 = addressService.getPossibleStreets("de navio");
		for(String one:possible8){
			System.out.println(one);
		}
		
		List<String> possible9 = addressService.getPossibleStreets("navio");
		for(String one:possible9){
			System.out.println(one);
		}
*/
		List<String> possible9 = addressService.getPossibleStreets("de herrera");
		for(String one:possible9){
			System.out.println(one);
		}
		
		List<String> possible5 = addressService.getPossibleStreets("alberto de herrera");
		for(String one:possible5){
			System.out.println(one);
		}
		
		List<String> possible4 = addressService.getPossibleStreets("avenida italia");
		for(String one:possible4){
			System.out.println(one);
		}
		
		List<String> possible3 = addressService.getPossibleStreets("rivera");
		for(String one:possible3){
			System.out.println(one);
		}
		
		assert(true);
		/*
		System.out.println(addressService.parseAddressToCoordinates("MARCO BRUTO", 1417, ""));
		System.out.println(addressService.parseAddressToCoordinates("MARCO BRUTO", "AVENIDA GENERAL RIVERA"));
		*/
	}


}
