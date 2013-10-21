package services.shapefiles.streets;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Street;
import model.dao.StreetDAO;

import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Geometry;

import com.vividsolutions.jts.geom.MultiLineString;

@Stateless(name = "StreetLoader")
public class StreetLoaderImpl implements StreetLoader {

	@EJB(beanName = "StreetDAO")
	protected StreetDAO streetDAO;

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
			while (reader.hasNext() && count<20) {
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
			throw new IllegalArgumentException("Malformed URL");
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private void saveStreet(MultiLineString multiLine, String streetName,
			Long nameCode) {
		
		Street street = new Street(streetName, nameCode.toString(),multiLine);
		Street old = streetDAO.find(streetName);
		if (old == null) {
			streetDAO.add(street);
		} else {
			street.setId(old.getId());
			//TODO add old segments to new ones multiLine.addAll(old.getSegments());
			streetDAO.modify(old, street);
		}
		System.out.println("Adding street " + streetName);
	}


	

}
