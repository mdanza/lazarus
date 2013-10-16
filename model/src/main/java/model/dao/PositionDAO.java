package model.dao;

import javax.ejb.Local;

import model.Position;

@Local
public interface PositionDAO extends ModelDAO<Position, String> {
	
	public Position findByLatitudeLongitude(double latitude, double longitude);

}
