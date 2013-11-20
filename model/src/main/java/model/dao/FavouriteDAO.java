package model.dao;

import java.util.List;

import javax.ejb.Local;

import model.Favourite;
import model.User;

@Local
public interface FavouriteDAO extends ModelDAO<Favourite, String> {
	
	public Favourite findByUserAndName(User user,String name);

	public List<Favourite> findByUser(User user);

}
