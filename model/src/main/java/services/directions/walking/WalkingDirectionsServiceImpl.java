package services.directions.walking;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import model.ShapefileWKT;
import model.Street;
import model.dao.StreetDAO;
import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.DefaultCoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "WalkingDirectionsService")
public class WalkingDirectionsServiceImpl implements WalkingDirectionsService {
	
	@EJB(beanName = "StreetDAO")
	protected StreetDAO streetDAO;
	
	@EJB(beanName = "CoordinateConverter")
	protected CoordinateConverter coordinateConverter;

	public Map<Coordinate, String> getWalkingDirections(Coordinate origin,
			Coordinate end) {
		GeometryFactory factory = new GeometryFactory();
		Point p = factory.createPoint(origin);
		try {
			Point newPoint = coordinateConverter.convertFromWGS84(p, ShapefileWKT.STREET);
			System.out.println(newPoint.getX());
			Street street = streetDAO.findClosestToPoint(newPoint);
			System.out.println(street.getName());
		} catch (MismatchedDimensionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	

}
