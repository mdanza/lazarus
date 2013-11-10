package services.directions.bus;

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
