package services.shapefiles;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;

/**
 * Prompts the user for a shapefile and displays the contents on the screen in a map frame.
 * <p>
 * This is the GeoTools Quickstart application used in documentationa and tutorials. *
 */
public class ShapefileLoader {

    /**
     * GeoTools Quickstart demo application. Prompts the user for a shapefile and displays its
     * contents on the screen in a map frame
     */
    public static void main(String[] args) throws Exception {
        // display a data store file chooser dialog for shapefiles
        File file = JFileDataStoreChooser.showOpenFile("dbf", null);
        if (file == null) {
            return;
        }

        
        
        FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
		FileChannel in = fileInputStream.getChannel();
        DbaseFileReader r = new DbaseFileReader( in, true, Charset.defaultCharset() );
        Object[] fields = new Object[r.getHeader().getNumFields()]; 
        int j=0;
        while (r.hasNext() && j<20) {
        	Object[] o = r.readEntry(fields); 
        	for(int i = 0;i<o.length;i++){
        		System.out.println(o[i]);
        	}
        	j++;
        }
        r.close();
        in.close();
        fileInputStream.close();
        
        
        
        
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        // Create a map content and add our shapefile to it
        MapContent map = new MapContent();
        map.setTitle("Quickstart");
        
        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);

        // Now display the map
        JMapFrame.showMap(map);
    }

}