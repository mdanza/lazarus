package services.directions.walking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Obstacle;
import model.dao.CornerDAO;
import model.dao.ObstacleDAO;
import model.dao.StreetDAO;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import services.directions.walking.utils.GoogleServicesAdapter;
import services.shapefiles.utils.CoordinateConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

@Stateless(name = "WalkingDirectionsService")
public class WalkingDirectionsServiceImpl implements WalkingDirectionsService {

	@EJB(beanName = "StreetDAO")
	protected StreetDAO streetDAO;

	@EJB(beanName = "CornerDAO")
	protected CornerDAO cornerDAO;

	@EJB(beanName = "ObstacleDAO")
	protected ObstacleDAO obstacleDAO;

	@EJB(beanName = "CoordinateConverter")
	protected CoordinateConverter coordinateConverter;

	public List<WalkingPosition> getWalkingDirections(Coordinate origin,
			Coordinate end) {
		try {
			List<List<WalkingPosition>> routes = GoogleServicesAdapter
					.getRoutes(origin, end);
			if (!routes.isEmpty()) {
				List<WalkingPosition> toReturn = new ArrayList<WalkingPosition>();
				List<WalkingPosition> route = routes.get(0);
				for (int i = 0; i < route.size() - 1; i++) {
					WalkingPosition first = route.get(i);
					WalkingPosition second = route.get(i + 1);

					// Create LineString to find near obstacles
					Coordinate firstCoordinate = first.getCoordinate();
					Coordinate secondCoordinate = second.getCoordinate();

					GeometryFactory factory = new GeometryFactory();
					Point firstPoint = coordinateConverter.convertFromWGS84(factory.createPoint(firstCoordinate),"obstacle");
					Point secondPoint = coordinateConverter.convertFromWGS84(factory.createPoint(secondCoordinate),"obstacle");
					
					List<Coordinate> coordinatesList = new ArrayList<Coordinate>();
					coordinatesList.add(firstPoint.getCoordinate());
					coordinatesList.add(secondPoint.getCoordinate());

					Coordinate[] coordinates = coordinatesList.toArray(new Coordinate[0]);
					CoordinateArraySequence coordinateArraySequence = new CoordinateArraySequence(coordinates);
					LineString lineRoute = new LineString(coordinateArraySequence, factory);
					
					// Find near obstacles
					List<Obstacle> nearObstacles = obstacleDAO.findByDistance(lineRoute, 30.0);
					
					if(nearObstacles!=null && !nearObstacles.isEmpty()){
						for(Obstacle obstacle:nearObstacles){
							Point centre = coordinateConverter.convertToWGS84(factory.createPoint(obstacle.getCentre().getCoordinate()),"obstacle");
							WalkingPosition walkingPosition = new WalkingPosition(centre.getCoordinate(), obstacle);
							if(!toReturn.contains(walkingPosition)){
								toReturn.add(walkingPosition);
							}
						}
					}
					
					toReturn.add(first);
					if(i==route.size()-2){
						toReturn.add(second);
					}

				}
				return toReturn;

			} else {
				return null;
			}
		} catch (MismatchedDimensionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (FactoryException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (TransformException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}
