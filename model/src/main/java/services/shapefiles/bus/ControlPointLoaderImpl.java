package services.shapefiles.bus;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.ControlPoint;
import model.ShapefileWKT;
import model.dao.ControlPointDAO;
import model.dao.ShapefileWKTDAO;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "ControlPointLoader")
public class ControlPointLoaderImpl implements ControlPointLoader {

	static Logger logger = Logger.getLogger(ControlPointLoaderImpl.class);

	@EJB(name = "ControlPointDAO")
	private ControlPointDAO controlPointDAO;

	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;

	public void updateShp(String url) {
		try {
			controlPointDAO.removeAll();
			ShapefileWKT shapefileWKT = shapefileWKTDAO
					.find(ShapefileWKT.CONTROL_POINT);
			URL shapeURL = new File(url).toURI().toURL();
			// get feature results
			ShapefileDataStore store = new ShapefileDataStore(shapeURL);
			FeatureReader reader = store.getFeatureReader();
			int count = 0;
			long total = store.getCount(Query.ALL);
			int propertyNumber;
			ControlPoint controlPoint;
			while (reader.hasNext()) {
				Feature feature = reader.next();
				propertyNumber = 0;
				controlPoint = new ControlPoint();

				Collection<? extends Property> values = feature.getValue();
				Iterator<? extends Property> valuesItr = values.iterator();
				while (valuesItr.hasNext()) {
					Property value = valuesItr.next();
					switch (propertyNumber) {
					case 0:
						controlPoint.setPoint((Point) value.getValue());
						break;
					case 1:
						controlPoint.setControlPointLocationCode(Integer
								.parseInt(value.getValue().toString()));
						break;
					case 3:
						controlPoint.setVariantCode(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 4:
						controlPoint.setOrdinal(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 5:
						controlPoint.setLocationDescription(value.getValue()
								.toString());
						break;
					case 6:
						controlPoint.setStreetCode((Long) value.getValue());
						break;
					case 7:
						controlPoint.setCornerStreetCode((Long) value
								.getValue());
						break;
					}
					propertyNumber++;
				}
				controlPointDAO.add(controlPoint);
				count++;
				if (shapefileWKT == null) {
					shapefileWKT = new ShapefileWKT();
					shapefileWKT.setShapefileType(ShapefileWKT.CONTROL_POINT);
					shapefileWKT.setWkt(feature.getDefaultGeometryProperty()
							.getDescriptor().getCoordinateReferenceSystem()
							.toWKT());
					shapefileWKTDAO.add(shapefileWKT);
				} else {
					shapefileWKT.setProgress((double) count / (double) total);
					shapefileWKTDAO.modify(shapefileWKT, shapefileWKT);
				}
				logger.info("added control point");
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
