package services.directions.walking;

import model.Obstacle;

import com.google.gson.annotations.Expose;
import com.vividsolutions.jts.geom.Coordinate;

public class WalkingPosition {

	private Coordinate coordinate;

	private String instruction;

	private Obstacle obstacle;

	public WalkingPosition() {
		super();
	}

	public WalkingPosition(Coordinate coordinate) {
		super();
		this.coordinate = coordinate;
	}

	public WalkingPosition(Coordinate coordinate, String instruction) {
		super();
		this.coordinate = coordinate;
		this.instruction = instruction;
	}

	public WalkingPosition(Coordinate coordinate, Obstacle obstacle) {
		super();
		this.coordinate = coordinate;
		this.obstacle = obstacle;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public Obstacle getObstacle() {
		return obstacle;
	}

	public void setObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

	@Override
	/**
	 * @returns true if the coordinates and instructions are the same, and if the obstacles have the same center coordinates, radius and description.
	 */
	public boolean equals(Object o) {
		boolean equals = false;
		if (o instanceof WalkingPosition) {
			WalkingPosition other = (WalkingPosition) o;
			boolean equalCoordinates = compareCoordinates(other);
			boolean equalInstructions = compareInstructions(other);
			boolean equalObstacles = compareObstacles(other);
			equals = equalCoordinates && equalInstructions && equalObstacles;
		}
		return equals;
	}

	private boolean compareObstacles(WalkingPosition other) {
		boolean equalObstacles = false;
		if (other.getObstacle() == null && this.getObstacle() == null) {
			equalObstacles = true;
		}
		if (this.obstacle != null && other.getObstacle() != null) {
			boolean equalCentre = false;
			if(this.obstacle.getCentre() == null && other.getObstacle().getCentre() == null){
				equalCentre = true;
			}
			if (this.obstacle.getCentre() != null && other.getObstacle().getCentre() != null) {
				if(this.obstacle.getCentre().getCoordinate()==null && other.getObstacle().getCentre().getCoordinate()==null){
					equalCentre = true;
				}
				if(this.obstacle.getCentre().getCoordinate()!=null){
					equalCentre = this.obstacle.getCentre().getCoordinate().equals(other.getObstacle().getCentre().getCoordinate());
				}
			}
			boolean equalRadius = false;
			if(this.obstacle.getRadius()==other.getObstacle().getRadius()){
				equalRadius = true;
			}
			boolean equalDescription = false;
			if(this.obstacle.getDescription()==null && other.getObstacle().getDescription()==null){
				equalDescription=true;
			}
			if(this.obstacle.getDescription()!=null){
				equalDescription = this.obstacle.getDescription().equals(other.getObstacle().getDescription());
			}
			equalObstacles = equalCentre && equalRadius && equalDescription;
		}
		return equalObstacles;
	}

	private boolean compareInstructions(WalkingPosition other) {
		boolean equalInstructions = false;
		if (other.getInstruction() == null && this.instruction == null) {
			equalInstructions = true;
		} else {
			if (this.instruction != null) {
				equalInstructions = this.instruction.equals(other
						.getInstruction());
			}
		}
		return equalInstructions;
	}

	private boolean compareCoordinates(WalkingPosition other) {
		boolean equalCoordinates = false;
		if (other.getCoordinate() == null && this.coordinate == null) {
			equalCoordinates = true;
		} else {
			if (this.coordinate != null) {
				equalCoordinates = this.coordinate.equals(other.getCoordinate());
			}
		}
		return equalCoordinates;
	}
}
