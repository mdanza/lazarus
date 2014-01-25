package services.streets;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;

import services.streets.abbreviations.AbbreviationService;

public class AbbreviationServiceLoaderTest {

	private AbbreviationService abbreviationService;

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

		abbreviationService = (AbbreviationService) context
				.lookup("AbbreviationServiceLocal");

	
	}

	@Test
	public void testParseAddress() {
		
//		abbreviationService.saveRouteTypes("C:/Users/Mateo/Downloads/vias_titulo_tipo_2/tipos_via.csv");
//		abbreviationService.saveRouteTitles("C:/Users/Mateo/Downloads/vias_titulo_tipo_2/titulos_vias.csv");
		
		
	
		 
	}


}
