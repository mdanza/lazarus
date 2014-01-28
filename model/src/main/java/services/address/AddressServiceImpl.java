package services.address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Address;
import model.Corner;
import model.Street;
import model.dao.AddressDAO;
import model.dao.CornerDAO;
import model.dao.StreetDAO;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.shapefiles.utils.CoordinateConverter;
import services.streets.abbreviations.AbbreviationService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "AddressService")
public class AddressServiceImpl implements AddressService {

	@EJB(beanName = "StreetDAO")
	protected StreetDAO streetDAO;

	@EJB(beanName = "CornerDAO")
	protected CornerDAO cornerDAO;

	@EJB(beanName = "AddressDAO")
	protected AddressDAO addressDAO;

	@EJB(beanName = "AbbreviationService")
	protected AbbreviationService abbreviationService;

	@EJB(beanName = "CoordinateConverter")
	protected CoordinateConverter coordinateConverter;

	public Coordinate parseAddressToCoordinates(String streetName,
			long addressNumber, String letter) {
		try {
			streetName = abbreviationService.abbreviate(streetName);
			Address address = addressDAO.findByStreetNameAndNumber(streetName,
					addressNumber, letter);
			if (address == null)
				throw new IllegalArgumentException("Address does not exist");
			Point point = coordinateConverter.convertToWGS84(
					address.getPoint(), "address");
			double x = point.getX();
			double y = point.getY();
			return new Coordinate(y, x);
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (FactoryException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (TransformException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public Coordinate parseAddressToCoordinates(String mainStreet,
			String cornerStreet) {
		try {
			mainStreet = abbreviationService.abbreviate(mainStreet);
			cornerStreet = abbreviationService.abbreviate(cornerStreet);
			List<Corner> corners = cornerDAO.findByStreetNames(mainStreet,
					cornerStreet);
			if (corners == null || corners.isEmpty())
				throw new IllegalArgumentException("Corner does not exist");
			Point point = coordinateConverter.convertToWGS84(corners.get(0)
					.getPoint(), "corner");
			double x = point.getX();
			double y = point.getY();
			return new Coordinate(y, x);
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (FactoryException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (TransformException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public List<String> getPossibleStreets(String approximate) {
		approximate = approximate.toUpperCase();
		approximate = abbreviationService.abbreviate(approximate);
		List<String> parts = Arrays.asList(approximate.split(" "));
		List<String> stringQueries;
		if (parts.size() < 6) {
			List<List<String>> possibleCombinations = getPossibleCombinations(parts);
			stringQueries = transformToQuery(possibleCombinations);
		} else {
			List<List<String>> notCombined = new ArrayList<List<String>>();
			notCombined.add(parts);
			stringQueries = transformToQuery(notCombined);
		}
		List<String> toReturn = new ArrayList<String>();
		for (String line : stringQueries) {
			List<String> possibleStreets = streetDAO.findPossibleStreets(line);
			toReturn.addAll(possibleStreets);
		}
		toReturn = removeDuplicates(toReturn);
		if (toReturn != null) {
			for (int i = 0; i < toReturn.size(); i++) {
				String street = toReturn.remove(i);
				street = abbreviationService.expandAbbreviations(street);
				toReturn.add(i,street);
			}
		}
		return toReturn;
	}

	private List<String> removeDuplicates(List<String> streets) {
		List<String> strings = new ArrayList<String>();
		for (String street : streets) {
			if (!strings.contains(street)) {
				strings.add(street);
			}
		}
		return strings;
	}

	private List<String> transformToQuery(
			List<List<String>> possibleCombinations) {
		List<String> toReturn = new ArrayList<String>();
		for (List<String> line : possibleCombinations) {
			StringBuilder builder = new StringBuilder();
			for (String word : line) {
				builder.append("%" + word + "%");
			}
			toReturn.add(builder.toString());
		}
		return toReturn;
	}

	private List<List<String>> getPossibleCombinations(List<String> values) {
		if (values.size() == 1) {
			ArrayList<List<String>> combination = new ArrayList<List<String>>();
			combination.add(values);
			return combination;
		} else {
			List<List<String>> newList = new ArrayList<List<String>>();
			for (String value : values) {
				// make a copy of the array
				List<String> rest = new ArrayList<String>(values);
				// remove the object
				rest.remove(value);
				newList.addAll(prependToEach(value,
						getPossibleCombinations(rest)));
			}
			return newList;
		}
	}

	private List<List<String>> prependToEach(String v, List<List<String>> vals) {
		for (List<String> o : vals) {
			o.add(0, v);
		}
		return vals;
	}

	public CloseLocationData getCloseLocationData(Coordinate coordinate) {
		try {
			double x = coordinate.x;
			double y = coordinate.y;
			coordinate.x = y;
			coordinate.y = x;
			GeometryFactory factory = new GeometryFactory();
			Point cornerPoint = coordinateConverter.convertFromWGS84(
					factory.createPoint(coordinate), "corner");
			Corner corner = cornerDAO.findClosestToPoint(cornerPoint);
			Point streetPoint = coordinateConverter.convertFromWGS84(
					factory.createPoint(coordinate), "street");
			Street street = streetDAO.findClosestToPoint(streetPoint);
			Corner copyCorner = expandAbbreviations(corner);
			Street copyStreet = expandAbbreviations(street);
			CloseLocationData closeData = new CloseLocationData();
			closeData.setClosestCorner(copyCorner);
			closeData.setClosestStreet(copyStreet);
			return closeData;
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (FactoryException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (TransformException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private Street expandAbbreviations(Street street) {
		Street newStreet = null;
		if (street != null) {
			newStreet = new Street();
			newStreet.setId(street.getId());
			newStreet.setName(abbreviationService.expandAbbreviations(street
					.getName()));
			newStreet.setNameCode(street.getNameCode());
			newStreet.setSegments(street.getSegments());
		}
		return newStreet;
	}

	private Corner expandAbbreviations(Corner corner) {
		Corner newCorner = null;
		if (corner != null) {
			newCorner = new Corner();
			newCorner.setFirstStreetName(abbreviationService
					.expandAbbreviations(corner.getFirstStreetName()));
			newCorner.setSecondStreetName(abbreviationService
					.expandAbbreviations(corner.getSecondStreetName()));
			newCorner.setId(corner.getId());
			newCorner.setFirstStreetNameCode(corner.getFirstStreetNameCode());
			newCorner.setPoint(corner.getPoint());
			newCorner.setSecondStreetNameCode(corner.getSecondStreetNameCode());
		}
		return newCorner;
	}

}
