package services.directions.bus;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;

import model.BusStop;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

@Local
public interface BusDirectionsService {

	public static final int precision = 200;

	public List<BusRide> getRoutes(Point startPoint, Point endPoint,
			int maxWalkingDistanceMeters);

	public List<Transshipment> getRoutesWithTransshipment(Point startPoint,
			Point endPoint, int maxWalkingDistanceMeters);

	public class BusRide {
		private String lineName;
		private String subLineDescription;
		private BusStop startStop;
		private BusStop endStop;
		private LineString trajectory;

		// constructor. Creates trajectory by sub-setting complete bus line
		// trajectory and limits it to the path between stops
		public BusRide(BusStop startStop, BusStop endStop,
				MultiLineString completeTrajectory, String lineName, String subLineDescription) {
			this.startStop = startStop;
			this.endStop = endStop;
			this.lineName = lineName;
			this.subLineDescription = subLineDescription;
			List<Coordinate> coordinateSubset = new ArrayList<Coordinate>();
			GeometryFactory geometryFactory = new GeometryFactory();
			Coordinate[] allCoordinates = completeTrajectory.getCoordinates();
			int i = 0;
			while (i < allCoordinates.length) {
				Coordinate c = allCoordinates[i];
				if (c.distance(startStop.getPoint().getCoordinate()) < precision)
					break;
				i++;
			}
			while (i < allCoordinates.length) {
				Coordinate c = allCoordinates[i];
				if (c.distance(endStop.getPoint().getCoordinate()) < precision) {
					coordinateSubset.add(c);
					break;
				}
				coordinateSubset.add(c);
				i++;
			}
			this.trajectory = geometryFactory
					.createLineString(coordinateSubset.toArray(new Coordinate[coordinateSubset.size()]));		
			}

		public BusStop getStartStop() {
			return startStop;
		}

		public void setStartStop(BusStop startStop) {
			this.startStop = startStop;
		}

		public BusStop getEndStop() {
			return endStop;
		}

		public void setEndStop(BusStop endStop) {
			this.endStop = endStop;
		}

		public LineString getTrajectory() {
			return trajectory;
		}

		public void setTrajectory(LineString trajectory) {
			this.trajectory = trajectory;
		}

		public String getLineName() {
			return lineName;
		}

		public void setLineName(String lineName) {
			this.lineName = lineName;
		}

		public String getSubLineDescription() {
			return subLineDescription;
		}

		public void setSubLineDescription(String subLineDescription) {
			this.subLineDescription = subLineDescription;
		}
	}

	public class Transshipment {
		private BusRide firstRoute;
		private BusRide secondRoute;

		public BusRide getFirstRoute() {
			return firstRoute;
		}

		public void setFirstRoute(BusRide firstRoute) {
			this.firstRoute = firstRoute;
		}

		public BusRide getSecondRoute() {
			return secondRoute;
		}

		public void setSecondRoute(BusRide secondRoute) {
			this.secondRoute = secondRoute;
		}
	}
}
