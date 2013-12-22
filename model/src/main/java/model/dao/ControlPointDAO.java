package model.dao;

import javax.ejb.Local;

import model.ControlPoint;

@Local
public interface ControlPointDAO extends ModelDAO<ControlPoint, Long> {
	public void removeAll();
}
