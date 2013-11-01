package services.directions.walking;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Corner;
import model.ShapefileWKT;
import model.Street;
import model.dao.CornerDAO;
import model.dao.StreetDAO;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.directions.walking.utils.GoogleServicesAdapter;
import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Stateless(name = "WalkingDirectionsService")
public class WalkingDirectionsServiceImpl implements WalkingDirectionsService {

	@EJB(beanName = "StreetDAO")
	protected StreetDAO streetDAO;
	
	@EJB(beanName = "CornerDAO")
	protected CornerDAO cornerDAO;

	@EJB(beanName = "CoordinateConverter")
	protected CoordinateConverter coordinateConverter;

	public List<Object[]> getWalkingDirections(Coordinate origin,
			Coordinate end) {
		try {
			// TODO Use alternative routes on obstacles
			List<List<Object[]>> routes = GoogleServicesAdapter.getRoutes(
					origin, end);
			if (!routes.isEmpty()) {
				/*
				GeometryFactory factory = new GeometryFactory();
				for (Coordinate coordinate : routes.get(0)) {
					System.out.println(coordinate.x+","+coordinate.y);
					Double x = coordinate.x;
					Double y = coordinate.y;
					coordinate.x = y;
					coordinate.y = x;
					Point routePoint = factory.createPoint(coordinate);
					Point shapePoint = coordinateConverter.convertFromWGS84(
							routePoint, ShapefileWKT.STREET);
					Street street = streetDAO.findClosestToPoint(shapePoint);
					List<Corner> corners = cornerDAO.findCornersWithinRadius(shapePoint, 5.0);					
					System.out.println( corners.size()+ " "+street.getName() + " " + x + "," + y);					
				}
				*/
				return routes.get(0);
			}else{
				return null;
			}
		} catch (MismatchedDimensionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}

}
