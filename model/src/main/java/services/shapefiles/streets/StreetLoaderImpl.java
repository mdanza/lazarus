package services.shapefiles.streets;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.ShapefileWKT;
import model.Street;
import model.dao.ShapefileWKTDAO;
import model.dao.StreetDAO;

import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

@Stateless(name = "StreetLoader")
public class StreetLoaderImpl implements StreetLoader {

	@EJB(beanName = "StreetDAO")
	protected StreetDAO streetDAO;
	
	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;
	
	GeometryFactory factory = new GeometryFactory();

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
							multiLine = (MultiLineString) value.getValue();
							break;
						case 1:
							streetName = (String) value.getValue();
							break;
						case 9:
							nameCode = (Long) value.getValue();
							break;
						}
					}
					position++;
				}
				saveStreet(multiLine, streetName, nameCode);
				count++;
				System.out.println(count);
			}
			reader = store.getFeatureReader();
			Feature feature = reader.next();
			ShapefileWKT shapefileWKT = new ShapefileWKT();
			shapefileWKT.setShapefileType(ShapefileWKT.STREET);
			shapefileWKT.setWkt(feature.getDefaultGeometryProperty()
					.getDescriptor().getCoordinateReferenceSystem().toWKT());
			shapefileWKTDAO.add(shapefileWKT);
			reader.close();

		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL");
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private void saveStreet(MultiLineString multiLine, String streetName,
			Long nameCode) {
		Street street = null;
		Street old = streetDAO.find(streetName);
		if (old == null) {
			street = new Street(streetName, nameCode.toString(),multiLine);
			streetDAO.add(street);
		}
	}


	

}
