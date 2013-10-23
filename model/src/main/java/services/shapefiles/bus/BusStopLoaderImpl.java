package services.shapefiles.bus;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.BusStop;
import model.dao.BusStopDAO;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "BusStopLoader")
public class BusStopLoaderImpl implements BusStopLoader {

	static Logger logger = Logger.getLogger(BusStopLoaderImpl.class);

	@EJB(name = "BusStopDAO")
	private BusStopDAO busStopDAO;

	public void readShp(String url) {
		try {
			URL shapeURL = new File(url).toURI().toURL();
			int count = 0;
			// get feature results
			ShapefileDataStore store = new ShapefileDataStore(shapeURL);
			FeatureReader reader = store.getFeatureReader();
			int propertyNumber;
			BusStop busStop;
			while (reader.hasNext()) {
				Feature feature = reader.next();
				propertyNumber = 0;
				busStop = new BusStop();

				Collection<? extends Property> values = feature.getValue();
				Iterator<? extends Property> valuesItr = values.iterator();
				while (valuesItr.hasNext()) {
					Property value = valuesItr.next();
					switch (propertyNumber) {
					case 0:
						busStop.setPoint((Point) value.getValue());
						break;
					case 1:
						busStop.setId(Integer.parseInt(value.getValue()
								.toString()));
						break;
					case 3:
						busStop.setBusRouteMaximalCode(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 4:
						busStop.setOrdinal(Integer.parseInt(value.getValue()
								.toString()));
						break;
					case 5:
						busStop.setStreetName(value.getValue().toString());
						break;
					case 6:
						busStop.setCornerStreetName(value.getValue().toString());
						break;
					case 7:
						busStop.setStreetCode((Long) value.getValue());
						break;
					case 8:
						busStop.setCornerStreetCode((Long) value.getValue());
						break;
					}
					propertyNumber++;
				}
				busStopDAO.add(busStop);
				count++;
				logger.info("added bus stop");
				System.out.println(count);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
