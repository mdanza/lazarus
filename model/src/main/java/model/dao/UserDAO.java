package model.dao;

import javax.ejb.Local;

import model.User;

@Local
public interface UserDAO extends ModelDAO<User, String> {

}
