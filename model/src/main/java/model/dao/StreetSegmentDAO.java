package model.dao;

import javax.ejb.Local;

import model.Position;
import model.StreetSegment;

@Local
public interface StreetSegmentDAO extends ModelDAO<StreetSegment, String>{

	public StreetSegment findByOriginEnd(Position origin, Position end);
}
