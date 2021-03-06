package services.streets.abbreviations;

import java.io.BufferedReader;
import java.io.File;
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
	public void saveRouteTypes(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
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
	public void saveRouteTitles(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
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
		if (street != null) {
			loadData();
			if (routeTypes != null) {
				for (RouteType routeType : routeTypes) {
					String abbreviation = routeType.getAbbreviateDescription();
					String description = routeType.getDescription();
					if (description != null && abbreviation != null) {
						street = abbreviate(street, description, abbreviation);
					}
				}
			}
			if (routeTitles != null) {
				for (RouteTitle routeTitle : routeTitles) {
					String abbreviation = routeTitle.getAbbreviateDescription();
					String description = routeTitle.getDescription();
					if (description != null && abbreviation != null) {
						street = abbreviate(street, description, abbreviation);
					}
				}
			}
		}
		return street;
	}

	private String abbreviate(String street, String description,
			String abbreviation) {
		String newStreet = street;
		if (street != null && description != null && abbreviation != null) {
			String[] partsOfStreet = street.split("\\ ");
			String[] partsOfDescription = description.split("\\ ");
			String[] partsOfAbbreviation = abbreviation.split("\\ ");
			if (partsOfDescription.length == 1) {
				for (int i = 0; i < partsOfStreet.length; i++) {
					if (partsOfStreet[i] != null
							&& partsOfStreet[i].equals(description)) {
						if (!description.equals("CALLE")) {
							partsOfStreet[i] = abbreviation;
						} else {
							partsOfStreet[i] = "";
						}
					}
				}
			} else {
				for (int i = 0; i < partsOfDescription.length; i++) {
					for (int j = 0; j < partsOfStreet.length; j++) {
						if (partsOfStreet[j] != null
								&& partsOfStreet[j]
										.equals(partsOfDescription[i])) {
							if (partsOfStreet[j].equals("DE")) {
								if (ofDescription(partsOfStreet,
										partsOfDescription, i, j)) {
									partsOfStreet[j] = "";
								}
							} else {
								if (partsOfAbbreviation.length == partsOfDescription.length) {
									partsOfStreet[j] = partsOfAbbreviation[i];
								} else {
									if (containsDeBefore(partsOfDescription, i)) {
										if (i - 1 < partsOfAbbreviation.length
												&& i - 1 > 0) {
											partsOfStreet[j] = partsOfAbbreviation[i - 1];
										} else {
											partsOfStreet[j] = "";
										}
									} else {
										partsOfStreet[j] = partsOfAbbreviation[i];
									}
								}
							}
						}
					}
				}
			}
			newStreet = createNewStreet(partsOfStreet);
		}
		return newStreet;
	}

	private String createNewStreet(String[] partsOfStreet) {
		String street = null;
		if (partsOfStreet != null && 0 < partsOfStreet.length) {
			StringBuilder stringBuilder = new StringBuilder();
			boolean firstWordAppended = false;
			for (String part : partsOfStreet) {
				if (part != null && !"".equals(part)) {
					if (!firstWordAppended) {
						stringBuilder.append(part);
						firstWordAppended = true;
					} else {
						stringBuilder.append(" ");
						stringBuilder.append(part);
					}
				}
			}
			street = stringBuilder.toString();
		}
		return street;
	}

	private boolean containsDeBefore(String[] partsOfDescription, int i) {
		boolean containsDeBefore = false;
		if (partsOfDescription != null && i > 0
				&& i < partsOfDescription.length) {
			for (int j = 0; j < i; j++) {
				if ("DE".equals(partsOfDescription[j])) {
					containsDeBefore = true;
				}
			}
		}
		return containsDeBefore;
	}

	private boolean ofDescription(String[] partsOfStreet,
			String[] partsOfDescription, int i, int j) {
		boolean ofDescription = false;
		if (partsOfStreet != null && partsOfDescription != null) {
			if (j < partsOfStreet.length - 1
					&& i < partsOfDescription.length - 1) {
				if ((partsOfStreet[j + 1] != null
						&& partsOfDescription[i + 1] != null && partsOfStreet[j + 1]
						.equals(partsOfDescription[i + 1]))) {
					ofDescription = true;
				}
			}
			if (j != 0 && i != 0) {
				if ((partsOfStreet[j - 1] != null
						&& partsOfDescription[i - 1] != null && partsOfStreet[j - 1]
						.equals(partsOfDescription[i - 1]))) {
					ofDescription = true;
				}
			}
		}
		return ofDescription;
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
		if (routeTypes != null) {
			for (int i = 0; i < routeTypes.size(); i++) {
				RouteType routeType = routeTypes.get(i);
				if (newRouteTypes.isEmpty()) {
					newRouteTypes.add(routeType);
				} else {
					boolean added = false;
					for (int j = 0; j < newRouteTypes.size(); j++) {
						if (!added) {
							RouteType newRouteType = newRouteTypes.get(j);
							if (newRouteType != null && routeType != null) {
								String description = routeType.getDescription();
								String newDescription = newRouteType
										.getDescription();
								if (description.split("\\ ").length > newDescription
										.split("\\ ").length) {
									added = true;
									newRouteTypes.add(j, routeType);
								}
								if (j == newRouteTypes.size() - 1 && !added) {
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
		if (routeTitles != null) {
			for (int i = 0; i < routeTitles.size(); i++) {
				RouteTitle routeTitle = routeTitles.get(i);
				if (newRouteTitles.isEmpty()) {
					newRouteTitles.add(routeTitle);
				} else {
					boolean added = false;
					for (int j = 0; j < newRouteTitles.size(); j++) {
						if (!added) {
							RouteTitle newRouteTitle = newRouteTitles.get(j);
							if (newRouteTitle != null && routeTitle != null) {
								String description = routeTitle
										.getDescription();
								String newDescription = newRouteTitle
										.getDescription();
								if (description.split("\\ ").length > newDescription
										.split("\\ ").length) {
									added = true;
									newRouteTitles.add(j, routeTitle);
								}
								if (j == newRouteTitles.size() - 1 && !added) {
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
					street = expand(street, abbreviation, description);
				}
			}
		}
		if (routeTitles != null) {
			for (RouteTitle routeTitle : routeTitles) {
				String abbreviation = routeTitle.getAbbreviateDescription();
				String description = routeTitle.getDescription();
				street = expand(street, abbreviation, description);
			}
		}
		return street;
	}

	private String expand(String street, String abbreviation, String description) {
		if (street != null) {
			String[] partsOfStreet = street.split("\\ ");
			for (int i = 0; i < partsOfStreet.length; i++) {
				String part = partsOfStreet[i];
				if (abbreviation != null && description != null) {
					if (abbreviation.split("\\ ").length == 1) {
						if (part.equals(abbreviation)) {
							partsOfStreet[i] = description;
						}
					} else {
						int length = abbreviation.split("\\ ").length;
						if (length + i < partsOfStreet.length) {
							boolean match = true;
							for (int j = 0; j < length; j++) {
								int positionInPartOfStreet = j + i;
								if (!abbreviation.split("\\ ")[j]
										.equals(partsOfStreet[positionInPartOfStreet])) {
									match = false;
								}
							}
							if (match) {
								if (description.split("\\ ").length == length) {
									for (int j = 0; j < length; j++) {
										int positionInPartOfStreet = j + i;
										partsOfStreet[positionInPartOfStreet] = description
												.split("\\ ")[j];
									}
								} else {
									partsOfStreet = instertInPartsOfStreet(
											partsOfStreet,
											description.split("\\ "), i,
											description.split("\\ ").length
													- length);
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

	private String[] instertInPartsOfStreet(String[] partsOfStreet,
			String[] toInstert, int indexToInsert,
			int numberOfStringsInPartsOfStreetToOverWrite) {
		String[] newPartsOfString = null;
		if (partsOfStreet != null && toInstert != null
				&& indexToInsert < partsOfStreet.length
				&& numberOfStringsInPartsOfStreetToOverWrite > 0) {
			newPartsOfString = new String[partsOfStreet.length
					+ (toInstert.length - 1 - numberOfStringsInPartsOfStreetToOverWrite)];
			int j = 0;
			for (int i = 0; i < newPartsOfString.length; i++) {
				if (i != indexToInsert) {
					if (j == 0) {
						newPartsOfString[i] = partsOfStreet[i];
					} else {
						newPartsOfString[i] = partsOfStreet[i
								- numberOfStringsInPartsOfStreetToOverWrite];
					}
				} else {
					for (j = 0; j < toInstert.length; j++) {
						newPartsOfString[i] = toInstert[j];
						if (j != toInstert.length - 1) {
							i++;
						}
					}
				}
			}
		}
		return newPartsOfString;
	}

}
