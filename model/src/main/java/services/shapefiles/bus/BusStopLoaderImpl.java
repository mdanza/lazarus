package services.shapefiles.bus;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.BusStop;
import model.ShapefileWKT;
import model.dao.BusStopDAO;
import model.dao.ShapefileWKTDAO;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import services.shapefiles.ShapefileStatusService;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "BusStopLoader")
public class BusStopLoaderImpl implements BusStopLoader {

	static Logger logger = Logger.getLogger(BusStopLoaderImpl.class);

	@EJB(name = "BusStopDAO")
	private BusStopDAO busStopDAO;

	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;

	@EJB(name = "ShapefileStatusService")
	private ShapefileStatusService shapefileStatusService;

	public void updateShp(File shapefile) {
		ShapefileDataStore store = null;
		try {
			busStopDAO.removeAll();
			ShapefileWKT shapefileWKT = shapefileWKTDAO
					.find(ShapefileWKT.BUS_STOP);
			URL shapeURL = shapefile.toURI().toURL();
			// get feature results
			store = new ShapefileDataStore(shapeURL);
			Charset encoding = store.getCharset();
			FeatureReader reader = store.getFeatureReader();
			int count = 0;
			long total = store.getCount(Query.ALL);
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
						busStop.setBusStopLocationCode(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 3:
						busStop.setVariantCode(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 4:
						busStop.setOrdinal(Integer.parseInt(value.getValue()
								.toString()));
						break;
					case 5:
						busStop.setStreetName(new String(value.getValue()
								.toString().getBytes(encoding)));
						break;
					case 6:
						busStop.setCornerStreetName(new String(value.getValue()
								.toString().getBytes(encoding)));
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
				if (shapefileWKT == null) {
					shapefileWKT = new ShapefileWKT();
					shapefileWKT.setShapefileType(ShapefileWKT.BUS_STOP);
					shapefileWKT.setWkt(feature.getDefaultGeometryProperty()
							.getDescriptor().getCoordinateReferenceSystem()
							.toWKT());
					shapefileWKTDAO.add(shapefileWKT);
				} else {
					double progress = (double) 100 * count / total;
					shapefileWKT.setProgress(progress);
					shapefileStatusService.setBusStopsUploadProgress(progress);
					shapefileWKTDAO.modify(shapefileWKT, shapefileWKT);
				}
				logger.info("added bus stop");
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (store != null)
				store.dispose();
		}
	}
}
