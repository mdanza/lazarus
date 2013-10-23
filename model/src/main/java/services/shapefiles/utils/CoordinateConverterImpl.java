package services.shapefiles.utils;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.ShapefileWKT;
import model.dao.ShapefileWKTDAO;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "CoordinateConverter")
public class CoordinateConverterImpl implements CoordinateConverter{

	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;

	public Point convertToWGS84(Point point, String type)
			throws FactoryException, MismatchedDimensionException,
			TransformException {
		String wkt = getWKTFromType(type);
		if (wkt != null) {
			CoordinateReferenceSystem crs = CRS.parseWKT(wkt);
			MathTransform mathTransform = CRS.findMathTransform(crs,
					DefaultGeographicCRS.WGS84, false);
			Point transformed = (Point) JTS.transform(point, mathTransform);
			return transformed;
		} else
			return null;
	}

	public Point convertFromWGS84(Point point, String type)
			throws FactoryException, MismatchedDimensionException,
			TransformException {
		String wkt = getWKTFromType(type);
		if (wkt != null) {
			CoordinateReferenceSystem crs = CRS.parseWKT(wkt);
			MathTransform mathTransform = CRS.findMathTransform(
					DefaultGeographicCRS.WGS84, crs, false);
			Point transformed = (Point) JTS.transform(point, mathTransform);
			return transformed;
		} else
			return null;
	}

	private String getWKTFromType(String type) {
		ShapefileWKT shapefileWKT = shapefileWKTDAO.find(type);
		if (shapefileWKT != null)
			return shapefileWKT.getWkt();
		else
			return null;
	}

}
