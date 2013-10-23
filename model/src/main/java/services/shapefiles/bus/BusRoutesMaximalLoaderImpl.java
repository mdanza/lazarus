package services.shapefiles.bus;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.BusRouteMaximal;
import model.dao.BusRouteMaximalDAO;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import com.vividsolutions.jts.geom.MultiLineString;

@Stateless(name = "BusRoutesMaximalLoader")
public class BusRoutesMaximalLoaderImpl implements BusRoutesMaximalLoader {

	static Logger logger = Logger.getLogger(BusRoutesMaximalLoaderImpl.class);

	@EJB(name = "BusRouteMaximalDAO")
	private BusRouteMaximalDAO busRouteMaximalDAO;

	public void readShp(String url) {
		try {
			URL shapeURL = new File(url).toURI().toURL();
			int count = 0;
			// get feature results
			ShapefileDataStore store = new ShapefileDataStore(shapeURL);
			FeatureReader reader = store.getFeatureReader();
			int propertyNumber;
			BusRouteMaximal busRouteMaximal;
			while (reader.hasNext()) {
				Feature feature = reader.next();
				propertyNumber = 0;
				busRouteMaximal = new BusRouteMaximal();

				Collection<? extends Property> values = feature.getValue();
				Iterator<? extends Property> valuesItr = values.iterator();
				while (valuesItr.hasNext()) {
					Property value = valuesItr.next();
					switch (propertyNumber) {
					case 0:
						busRouteMaximal.setTrajectory((MultiLineString) value
								.getValue());
						break;
					case 3:
						busRouteMaximal
								.setLineName(value.getValue().toString());
						break;
					case 7:
						busRouteMaximal.setId(Integer.parseInt(value.getValue()
								.toString()));
						break;
					case 9:
						busRouteMaximal
								.setMaximalCode(value.getValue() == null ? -1
										: Integer.parseInt(value.getValue()
												.toString()));
						break;
					}
					propertyNumber++;
				}
				busRouteMaximalDAO.add(busRouteMaximal);
				count++;
				logger.info("added bus route maximal");
				System.out.println(count);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
