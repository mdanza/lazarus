package services.streets.abbreviations;

import java.io.File;

import javax.ejb.Local;

@Local
public interface AbbreviationService {

	public void saveRouteTypes(File file);

	public void saveRouteTitles(File file);

	public String abbreviate(String street);

	public String expandAbbreviations(String street);
}
