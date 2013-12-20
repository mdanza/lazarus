package model.dao;

import javax.ejb.Local;

import model.Bus;

@Local
public interface BusDAO extends ModelDAO<Bus, Long> {

}
