package services.shapefiles.utils;

import javax.ejb.Local;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

@Local
public interface CoordinateConverter {
	public Point convertToWGS84(Point point, String type) throws FactoryException, MismatchedDimensionException, TransformException;
	public Point convertFromWGS84(Point point, String type) throws FactoryException, MismatchedDimensionException, TransformException;
}
