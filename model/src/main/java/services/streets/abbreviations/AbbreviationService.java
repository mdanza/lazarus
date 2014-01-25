package services.streets.abbreviations;

import javax.ejb.Local;

@Local
public interface AbbreviationService {

	public void saveRouteTypes(String url);
	
	public void saveRouteTitles(String url);
	
	public String abbreviate(String street);
	
	public String expandAbbreviations(String street);
}
