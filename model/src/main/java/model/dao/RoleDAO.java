package model.dao;

import javax.ejb.Local;

import model.Role;

@Local
public interface RoleDAO extends ModelDAO<Role, String> {

}
