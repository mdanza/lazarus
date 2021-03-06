package services.address;

import model.Corner;
import model.Street;

public class CloseLocationData {

	private Street closestStreet;

	private Corner closestCorner;

	public Street getClosestStreet() {
		return closestStreet;
	}

	public void setClosestStreet(Street closestStreet) {
		this.closestStreet = closestStreet;
	}

	public Corner getClosestCorner() {
		return closestCorner;
	}

	public void setClosestCorner(Corner closestCorner) {
		this.closestCorner = closestCorner;
	}

}
