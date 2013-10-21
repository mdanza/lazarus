package services.shapefiles.address;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Address;
import model.dao.AddressDAO;

import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Geometry;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "AddressLoader")
public class AddressLoaderImpl implements AddressLoader {

	@EJB(beanName = "AddressDAO")
	protected AddressDAO addressDAO;
	
	GeometryFactory factory = new GeometryFactory();

	public void readShp(String url) {
		try {
			URL shapeURL = new File(url).toURI().toURL();
			ShapefileDataStore store = new ShapefileDataStore(shapeURL);
			FeatureReader reader = store.getFeatureReader();
			int count = 0;
			int position = 0;
			Point point = null;
			long padron = 0;
			long nameCode = 0;
			String streetName = null;
			int number = 0;
			String letter = null;
			String paridad = null;
			while (reader.hasNext()) {
				Feature feature = reader.next();
				Collection<? extends Property> values = feature.getValue();
				Iterator<? extends Property> valuesItr = values.iterator();
				position = 0;
				while (valuesItr.hasNext()) {
					Property value = valuesItr.next();
					if (!(value instanceof Geometry)) {
						switch (position) {
						case 0:
							point = (Point) value.getValue();
							break;
						case 1:
							padron = (Long) value.getValue();
							break;
						case 2:
							nameCode = (Long) value.getValue();
							break;
						case 3:
							streetName = (String) value.getValue();
							break;
						case 4:
							number = (Integer) value.getValue();
							break;
						case 5:
							letter = (String) value.getValue();
							break;
						case 6:
							paridad = (String) value.getValue();
							break;							
						}
					}
					position++;
				}
				saveAddress(point, padron, nameCode, streetName, number, letter, paridad);
				count++;
				System.out.println(count);
			}

			reader.close();

		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL");
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private void saveAddress(Point point, long padron, long nameCode,
			String streetName, int number, String letter, String paridad) {
			Address address = new Address(point,padron,nameCode,streetName,number,letter,paridad);
			addressDAO.add(address);
	}
}
