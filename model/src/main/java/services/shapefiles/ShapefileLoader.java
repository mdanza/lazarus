package services.shapefiles;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.geometry.Geometry;

public class ShapefileLoader {

	public static void readShp(String url) throws IOException,
			URISyntaxException {
		URL shapeURL = new File(url).toURI().toURL();

		// get feature results
		ShapefileDataStore store = new ShapefileDataStore(shapeURL);
		String name = store.getTypeNames()[0];
		FeatureSource source = store.getFeatureSource(name);
		FeatureCollection fsShape = source.getFeatures();

		// print out a feature type header and wait for user input
		FeatureType ft = fsShape.getSchema();
		System.out.println("FID\t");

		Collection<PropertyDescriptor> properties = ft.getDescriptors();
		System.out.println();
		int propCount = properties.size();
		System.out.println();
		Iterator<PropertyDescriptor> propertiesItr = properties.iterator();
		while (propertiesItr.hasNext()) {
			PropertyDescriptor property = propertiesItr.next();

			PropertyType at = property.getType();

			if (!Geometry.class.isAssignableFrom(at.getClass())) {
				System.out.print(at.getName() + "\t");
			}
		}

		// now print out the feature contents (every non geometric attribute)
		FeatureReader reader = store.getFeatureReader();
		int count = 0;
		while (reader.hasNext()) {
			Feature feature = reader.next();
			System.out.print(feature.getIdentifier().getID() + "\t");
			Collection<? extends Property> values = feature.getValue();
			Iterator<? extends Property> valuesItr = values.iterator();
			while (valuesItr.hasNext()) {
				Property value = valuesItr.next();

				if (!(value instanceof Geometry)) {
					System.out.print(value + "\t");
				}
			}

			System.out.println();
			count++;
		}

		reader.close();
		System.out.println();
		System.out.println();
		System.out.println();

		// and finally print out every geometry in wkt format
		reader = store.getFeatureReader();

		while (reader.hasNext()) {
			Feature feature = reader.next();
			System.out.print(feature.getIdentifier().getID() + "\t");
			System.out.println(feature.getDefaultGeometryProperty());
			System.out.println();
		}
		
		reader.close();
		System.out.println();
		System.out.println("ROW COUNT:" + count);
		System.out.println("PROPERTIES COUNT:" + propCount);
		System.out.println();
	}
}