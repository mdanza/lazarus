package services.shapefiles.bus;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.BusRouteNonMaximal;
import model.dao.BusRouteNonMaximalDAO;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import com.vividsolutions.jts.geom.MultiLineString;

@Stateless(name = "BusRoutesLoader")
public class BusRoutesNonMaximalLoaderImpl implements BusRoutesNonMaximalLoader {

	static Logger logger = Logger.getLogger(BusRoutesNonMaximalLoaderImpl.class);

	@EJB(name = "BusRouteNonMaximalDAO")
	private BusRouteNonMaximalDAO busRouteNonMaximalDAO;

	public void readShp(String url) {
		try {
			URL shapeURL = new File(url).toURI().toURL();
			int count = 0;
			// get feature results
			ShapefileDataStore store = new ShapefileDataStore(shapeURL);
			FeatureReader reader = store.getFeatureReader();
			int propertyNumber;
			BusRouteNonMaximal busRouteNonMaximal;
			while (reader.hasNext() && count < 20) {
				Feature feature = reader.next();
				propertyNumber = 0;
				busRouteNonMaximal = new BusRouteNonMaximal();

				Collection<? extends Property> values = feature.getValue();
				Iterator<? extends Property> valuesItr = values.iterator();
				while (valuesItr.hasNext()) {
					Property value = valuesItr.next();
					switch (propertyNumber) {
					case 0:
						busRouteNonMaximal
								.setTrajectory((MultiLineString) value
										.getValue());
						break;
					case 2:
						busRouteNonMaximal.setId(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 3:
						busRouteNonMaximal.setVariantCode(Integer
								.parseInt(value.getValue().toString()));
						break;
					case 4:
						busRouteNonMaximal.setDescription(value.getValue()
								.toString());
						break;
					case 5:
						busRouteNonMaximal.setOriginBusStopCode(Integer
								.parseInt(value.getValue().toString()));
						break;
					case 6:
						busRouteNonMaximal.setEndBusStopCode(Integer
								.parseInt(value.getValue().toString()));
						break;
					case 7:
						busRouteNonMaximal.setOriginBusStopOrdinal(Integer
								.parseInt(value.getValue().toString()));
						break;
					case 8:
						busRouteNonMaximal.setEndBusStopOrdinal(Integer
								.parseInt(value.getValue().toString()));
						break;
					}
					propertyNumber++;
				}
				busRouteNonMaximalDAO.add(busRouteNonMaximal);
				count++;
				logger.info("added bus route non maximal");
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
