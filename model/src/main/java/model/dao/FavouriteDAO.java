package model.dao;

import javax.ejb.Local;

import model.Favourite;
import model.User;

@Local
public interface FavouriteDAO extends ModelDAO<Favourite, String> {
	
	public Favourite findByUserAndName(User user,String name);

}
