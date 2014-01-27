package services.shapefiles.bus;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.BusRouteMaximal;
import model.ShapefileWKT;
import model.dao.BusRouteMaximalDAO;
import model.dao.ShapefileWKTDAO;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import services.shapefiles.ShapefileStatusService;

import com.vividsolutions.jts.geom.MultiLineString;

@Stateless(name = "BusRoutesMaximalLoader")
public class BusRoutesMaximalLoaderImpl implements BusRoutesMaximalLoader {

	static Logger logger = Logger.getLogger(BusRoutesMaximalLoaderImpl.class);

	@EJB(name = "BusRouteMaximalDAO")
	private BusRouteMaximalDAO busRouteMaximalDAO;

	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;

	@EJB(name = "ShapefileStatusService")
	private ShapefileStatusService shapefileStatusService;

	public void updateShp(File shapefile) {
		ShapefileDataStore store = null;
		try {
			busRouteMaximalDAO.removeAll();
			ShapefileWKT shapefileWKT = shapefileWKTDAO
					.find(ShapefileWKT.BUS_MAXIMAL);
			URL shapeURL = shapefile.toURI().toURL();
			// get feature results
			store = new ShapefileDataStore(shapeURL);
			Charset encoding = store.getCharset();
			FeatureReader reader = store.getFeatureReader();
			int count = 0;
			long total = store.getCount(Query.ALL);
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
						busRouteMaximal.setLineName(new String(value.getValue()
								.toString().getBytes(encoding)));
						break;
					case 5:
						busRouteMaximal.setSubLineCode(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 6:
						busRouteMaximal.setSubLineDescription(new String(value
								.getValue().toString().getBytes(encoding)));
						break;
					case 7:
						busRouteMaximal.setVariantCode(Integer.parseInt(value
								.getValue().toString()));
						break;
					case 9:
						if (value != null && value.getValue() != null)
							busRouteMaximal.setMaximalVariantCode(Integer
									.parseInt(value.getValue().toString()));
						else
							busRouteMaximal.setMaximalVariantCode(-1);
						break;
					case 13:
						busRouteMaximal.setDestination(new String(value
								.getValue().toString().getBytes(encoding)));
						break;
					}
					propertyNumber++;
				}
				busRouteMaximalDAO.add(busRouteMaximal);
				count++;
				if (shapefileWKT == null) {
					shapefileWKT = new ShapefileWKT();
					shapefileWKT.setShapefileType(ShapefileWKT.BUS_MAXIMAL);
					shapefileWKT.setWkt(feature.getDefaultGeometryProperty()
							.getDescriptor().getCoordinateReferenceSystem()
							.toWKT());
					shapefileWKTDAO.add(shapefileWKT);
				} else {
					double progress = (double) 100 * count / total;
					shapefileWKT.setProgress(progress);
					shapefileStatusService
							.setBusRouteMaximalUploadProgress(progress);
					shapefileWKTDAO.modify(shapefileWKT, shapefileWKT);
				}
				logger.info("added bus route maximal");
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
