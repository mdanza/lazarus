package services.shapefiles.streets;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Position;
import model.Street;
import model.StreetSegment;
import model.dao.PositionDAO;
import model.dao.StreetDAO;
import model.dao.StreetSegmentDAO;

import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiLineString;

@Stateless(name = "StreetLoader")
public class StreetLoaderImpl implements StreetLoader {

	@EJB(beanName = "StreetDAO")
	protected StreetDAO streetDAO;

	@EJB(beanName = "StreetSegmentDAO")
	protected StreetSegmentDAO streetSegmentDAO;

	@EJB(name = "PositionDAO")
	private PositionDAO positionDAO;

	public void readShp(String url) {
		try {
			URL shapeURL = new File(url).toURI().toURL();
			ShapefileDataStore store = new ShapefileDataStore(shapeURL);
			FeatureReader reader = store.getFeatureReader();
			int count = 0;
			int position = 0;
			MultiLineString multiLine = null;
			String streetName = null;
			Long nameCode = null;
			while (reader.hasNext() && count <= 20) {
				Feature feature = reader.next();
				Collection<? extends Property> values = feature.getValue();
				Iterator<? extends Property> valuesItr = values.iterator();
				position = 0;
				while (valuesItr.hasNext()) {
					Property value = valuesItr.next();
					if (!(value instanceof Geometry)) {
						switch (position) {
						case 0:
							multiLine = (MultiLineString) value.getValue();
							break;
						case 1:
							streetName = (String) value.getValue();
							break;
						case 9:
							nameCode = (Long) value.getValue();
						}
					}
					position++;
				}
				saveStreet(multiLine, streetName, nameCode);

				count++;
			}

			reader.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveStreet(MultiLineString multiLine, String streetName,
			Long nameCode) {
		Coordinate[] coordinates = multiLine.getCoordinates();
		double originLongitude;
		double originLatitude;
		double endLongitude;
		double endLatitude;
		ArrayList<StreetSegment> streetSegments = new ArrayList<StreetSegment>();
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates.length != i + 1) {
				originLongitude = coordinates[i].x;
				originLatitude = coordinates[i].y;
				Position origin = positionDAO.findByLatitudeLongitude(
						originLatitude, originLongitude);
				if (origin == null) {
					origin = new Position(originLatitude, originLongitude);
					positionDAO.add(origin);
				}
				endLongitude = coordinates[i].x;
				endLatitude = coordinates[i].y;
				Position end = positionDAO.findByLatitudeLongitude(endLatitude,
						endLongitude);
				if (end == null) {
					end = new Position(endLatitude, endLongitude);
					positionDAO.add(end);
				}
				StreetSegment streetSegment = streetSegmentDAO.findByOriginEnd(
						origin, end);
				if (streetSegment == null) {
					streetSegment = new StreetSegment(origin,
							end);
					streetSegmentDAO.add(streetSegment);
				}
				streetSegments.add(streetSegment);
				
			}
		}
		Street street = new Street(streetName, nameCode.toString(),
				streetSegments);
		for (StreetSegment segment : streetSegments) {
			/*
			StreetSegment streetSegment = streetSegmentDAO.findByOriginEnd(
					segment.getOrigin(), segment.getEnd());
			if (streetSegment == null) {
				streetSegment = new StreetSegment(segment.getOrigin(),
						segment.getEnd());
				streetSegmentDAO.add(streetSegment);
			}
			*/
		}
		Street old = streetDAO.find(streetName);
		if (old == null) {
			streetDAO.add(street);
		} else {
			street.setId(old.getId());
			List<StreetSegment> segments = street.getStreetSegments();
			segments.addAll(old.getStreetSegments());
			street.setStreetSegments(segments);
			streetDAO.modify(old, street);
		}
		System.out.println("Adding street " + streetName);

	}

}
