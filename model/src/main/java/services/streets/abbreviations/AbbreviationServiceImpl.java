package services.streets.abbreviations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.RouteTitle;
import model.RouteType;
import model.dao.RouteTitleDAO;
import model.dao.RouteTypeDAO;

@Stateless(name = "AbbreviationService")
public class AbbreviationServiceImpl implements AbbreviationService {

	@EJB(beanName = "RouteTypeDAO")
	protected RouteTypeDAO routeTypeDAO;

	@EJB(beanName = "RouteTitleDAO")
	protected RouteTitleDAO routeTitleDAO;

	private List<RouteType> routeTypes;

	private List<RouteTitle> routeTitles;

	@Override
	public void saveRouteTypes(String url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(url));
			String line = reader.readLine();
			routeTypeDAO.removeAll();
			for (line = reader.readLine(); line != null; line = reader
					.readLine()) {
				if (line.trim().length() > 0) {
					String elements[] = line.split("\\,");
					if (elements.length == 3) {
						String description = elements[1];
						String abbreviateDescription = elements[2];
						description = description.replaceAll("\"", "");
						abbreviateDescription = abbreviateDescription
								.replaceAll("\"", "");
						RouteType routeType = new RouteType(description,
								abbreviateDescription);
						routeTypeDAO.add(routeType);
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException();
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		} catch (IOException e) {
			throw new IllegalArgumentException();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

	@Override
	public void saveRouteTitles(String url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(url));
			String line = reader.readLine();
			routeTitleDAO.removeAll();
			for (line = reader.readLine(); line != null; line = reader
					.readLine()) {
				if (line.trim().length() > 0) {
					String elements[] = line.split("\\,");
					if (elements.length == 3) {
						String description = elements[1];
						String abbreviateDescription = elements[2];
						description = description.replaceAll("\"", "");
						abbreviateDescription = abbreviateDescription
								.replaceAll("\"", "");
						RouteTitle routeTitle = new RouteTitle(description,
								abbreviateDescription);
						routeTitleDAO.add(routeTitle);
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException();
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		} catch (IOException e) {
			throw new IllegalArgumentException();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new IllegalArgumentException();
				}
			}
		}

	}

	public String abbreviate(String street) {
		loadData();
		if (routeTypes != null) {
			for (RouteType routeType : routeTypes) {
				String abbreviation = routeType.getAbbreviateDescription();
				String description = routeType.getDescription();
				if (!"CALLE".equals(description)) {
					street = replace(street, description, abbreviation);
				}
			}
		}
		if (routeTitles != null) {
			for (RouteTitle routeTitle : routeTitles) {
				String abbreviation = routeTitle.getAbbreviateDescription();
				String description = routeTitle.getDescription();
				street = replace(street, description, abbreviation);
			}
		}
		return street;
	}

	private String constructStreet(String[] partsOfStreet) {
		String street = null;
		if (partsOfStreet != null) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < partsOfStreet.length; i++) {
				String part = partsOfStreet[i];
				stringBuilder.append(part);
				if (i < partsOfStreet.length - 1) {
					stringBuilder.append(" ");
				}
			}
			street = stringBuilder.toString();
		}
		return street;
	}

	private void loadData() {
		if (routeTypes == null) {
			routeTypes = routeTypeDAO.getAll();
			routeTypes = getRouteTypesOrderedByNumberOfWords(routeTypes);
		}
		if (routeTitles == null) {
			routeTitles = routeTitleDAO.getAll();
			routeTitles = getRouteTitlesOrderedByNumberOfWords(routeTitles);
		}
	}

	private List<RouteType> getRouteTypesOrderedByNumberOfWords(
			List<RouteType> routeTypes) {
		List<RouteType> newRouteTypes = new ArrayList<RouteType>();
		if(routeTypes!=null){
			for(int i = 0;i<routeTypes.size();i++){
				RouteType routeType = routeTypes.get(i);
				if(newRouteTypes.isEmpty()){
					newRouteTypes.add(routeType);
				}else{
					boolean added = false;
					for(int j = 0;j<newRouteTypes.size();j++){
						if(!added){
						RouteType newRouteType = newRouteTypes.get(j);
						if(newRouteType!=null && routeType!=null){
							String description = routeType.getDescription();
							String newDescription = newRouteType.getDescription();
							if(description.split("\\ ").length>newDescription.split("\\ ").length){
								added = true;
								newRouteTypes.add(j,routeType);
							}
							if(j==newRouteTypes.size()-1 && !added){
								added = true;
								newRouteTypes.add(routeType);
							}
						}
						}
					}
				}
			}
		}
		return newRouteTypes;
	}

	private List<RouteTitle> getRouteTitlesOrderedByNumberOfWords(
			List<RouteTitle> routeTitles) {
		List<RouteTitle> newRouteTitles = new ArrayList<RouteTitle>();
		if(routeTitles!=null){
			for(int i = 0;i<routeTitles.size();i++){
				RouteTitle routeTitle = routeTitles.get(i);
				if(newRouteTitles.isEmpty()){
					newRouteTitles.add(routeTitle);
				}else{
					boolean added = false;
					for(int j = 0;j<newRouteTitles.size();j++){
						if(!added){
						RouteTitle newRouteTitle = newRouteTitles.get(j);
						if(newRouteTitle!=null && routeTitle!=null){
							String description = routeTitle.getDescription();
							String newDescription = newRouteTitle.getDescription();
							if(description.split("\\ ").length>newDescription.split("\\ ").length){
								added = true;
								newRouteTitles.add(j,routeTitle);
							}
							if(j==newRouteTitles.size()-1 && !added){
								added = true;
								newRouteTitles.add(routeTitle);
							}
						}
						}
					}
				}
			}
		}
		return newRouteTitles;
	}
	
	
	public String expandAbbreviations(String street) {
		loadData();
		if (routeTypes != null) {
			for (RouteType routeType : routeTypes) {
				String abbreviation = routeType.getAbbreviateDescription();
				String description = routeType.getDescription();
				if (!"CALLE".equals(description)) {
					street = replace(street, abbreviation, description);
				}
			}
		}
		if (routeTitles != null) {
			for (RouteTitle routeTitle : routeTitles) {
				String abbreviation = routeTitle.getAbbreviateDescription();
				String description = routeTitle.getDescription();
				street = replace(street, abbreviation, description);
			}
		}
		return street;
	}

	private String replace(String street, String toMatch, String toReplace) {
		if (street != null) {
			String[] partsOfStreet = street.split("\\ ");
			for (int i = 0; i < partsOfStreet.length; i++) {
				String part = partsOfStreet[i];
				if (toMatch != null && toReplace != null) {
					if (toMatch.split("\\ ").length == 1) {
						if (part.equals(toMatch)) {
							partsOfStreet[i] = toReplace;
						}
					} else {
						int length = toMatch.split("\\ ").length;
						if (length + i < partsOfStreet.length) {
							boolean match = true;
							for (int j = 0; j < length; j++) {
								int positionInPartOfStreet = j + i;
								if (!toMatch.split("\\ ")[j].equals(partsOfStreet[positionInPartOfStreet])) {
									match = false;
								}
							}
							if(match){
								for (int j = 0; j < length; j++) {
									int positionInPartOfStreet = j + i;
									partsOfStreet[positionInPartOfStreet] = toReplace.split("\\ ")[j];
								}
							}
						}
						
					}
				}
			}
			street = constructStreet(partsOfStreet);
		}
		return street;
	}
}
