package services.users;

import javax.ejb.Local;

import model.User;

import com.vividsolutions.jts.geom.Point;

@Local
public interface FavouriteService {

	public void addToFavourite(User user, Point point, String name);

	public void removeFromFavourite(User user, String name);

}
