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
		private int subLineCode;
		private BusStop startStop;
		private BusStop endStop;
		private LineString trajectory;
		private List<BusStop> previousStops;

		// constructor. Creates trajectory by sub-setting complete bus line
		// trajectory and limits it to the path between stops
		public BusRide(BusStop startStop, BusStop endStop,
				MultiLineString completeTrajectory, String lineName,
				String subLineDescription, int subLineCode,
				BusStop previousStop, BusStop secondPreviousStop) {
			this.startStop = startStop;
			this.endStop = endStop;
			this.lineName = lineName;
			this.subLineDescription = subLineDescription;
			this.subLineCode = subLineCode;
			this.previousStops = new ArrayList<BusStop>();
			previousStops.add(previousStop);
			previousStops.add(secondPreviousStop);
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
			this.trajectory = geometryFactory.createLineString(coordinateSubset
					.toArray(new Coordinate[coordinateSubset.size()]));
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

		public int getSubLineCode() {
			return subLineCode;
		}

		public void setSubLineCode(int subLineCode) {
			this.subLineCode = subLineCode;
		}

		public List<BusStop> getPreviousStops() {
			return previousStops;
		}

		public void setPreviousStops(List<BusStop> previousStops) {
			this.previousStops = previousStops;
		}
	}

	public class Transshipment {
		private BusRide firstRoute;
		private BusRide secondRoute;

		public Transshipment(BusRide firstRoute, BusRide secondRoute) {
			this.firstRoute = firstRoute;
			this.secondRoute = secondRoute;
		}

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
