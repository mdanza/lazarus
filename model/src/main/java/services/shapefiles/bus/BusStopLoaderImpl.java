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
			while (reader.hasNext() && count < 20) {
				Feature feature = reader.next();
				propertyNumber = 0;
				busStop = new BusStop();

				Collection<? extends Property> values = feature.getValue();
				Iterator<? extends Property> valuesItr = values.iterator();
				while (valuesItr.hasNext()) {
					Property value = valuesItr.next();
					switch (propertyNumber) {
					case 3:
						busStop.setVariantCode(Integer.parseInt(value
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
						busStop.setStreetCode((Integer) value.getValue());
						break;
					case 8:
						busStop.setCornerStreetCode((Integer) value.getValue());
						break;
					case 9:
						busStop.setX(Double.parseDouble(value.getValue()
								.toString()));
						break;
					case 10:
						busStop.setY(Double.parseDouble(value.getValue()
								.toString()));
						break;
					}
				}
				busStopDAO.add(busStop);
				count++;
				logger.info("added bus stop");
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
