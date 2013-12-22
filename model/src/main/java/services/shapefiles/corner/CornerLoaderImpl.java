package services.shapefiles.corner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Corner;
import model.ShapefileWKT;
import model.dao.CornerDAO;
import model.dao.ShapefileWKTDAO;

import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.geometry.Geometry;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "CornerLoader")
public class CornerLoaderImpl implements CornerLoader {

	@EJB(beanName = "CornerDAO")
	protected CornerDAO cornerDAO;

	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;

	GeometryFactory factory = new GeometryFactory();

	public void updateShp(String url) {
		try {
			cornerDAO.removeAll();
			ShapefileWKT shapefileWKT = shapefileWKTDAO
					.find(ShapefileWKT.CORNER);
			URL shapeURL = new File(url).toURI().toURL();
			ShapefileDataStore store = new ShapefileDataStore(shapeURL);
			FeatureReader reader = store.getFeatureReader();
			int count = 0;
			long total = store.getCount(Query.ALL);
			int position = 0;
			Point point = null;
			long firstStreetNameCode = 0;
			long secondStreetNameCode = 0;
			String firstStreetName = null;
			String secondStreetName = null;
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
							firstStreetNameCode = (Long) value.getValue();
							break;
						case 2:
							secondStreetNameCode = (Long) value.getValue();
							break;
						case 3:
							firstStreetName = (String) value.getValue();
							break;
						case 4:
							secondStreetName = (String) value.getValue();
							break;
						}
					}
					position++;
				}
				saveCorner(point, firstStreetNameCode, secondStreetNameCode,
						firstStreetName, secondStreetName);
				count++;
				if (shapefileWKT == null) {
					shapefileWKT = new ShapefileWKT();
					shapefileWKT.setShapefileType(ShapefileWKT.CORNER);
					shapefileWKT.setWkt(feature.getDefaultGeometryProperty()
							.getDescriptor().getCoordinateReferenceSystem()
							.toWKT());
					shapefileWKTDAO.add(shapefileWKT);
				} else {
					shapefileWKT.setProgress((double) count / (double) total);
					shapefileWKTDAO.modify(shapefileWKT, shapefileWKT);
				}
				System.out.println(count);
			}
			reader.close();

		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL");
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private void saveCorner(Point point, long firstStreetNameCode,
			long secondStreetNameCode, String firstStreetName,
			String secondStreetName) {
		Corner corner = new Corner(point, firstStreetNameCode,
				secondStreetNameCode, firstStreetName, secondStreetName);
		cornerDAO.add(corner);

	}

}
