package services.users;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Favourite;
import model.User;
import model.dao.FavouriteDAO;

import com.vividsolutions.jts.geom.Point;

@Stateless(name = "FavouriteService")
public class FavouriteServiceImpl implements FavouriteService {

	@EJB(beanName = "FavouriteDAO")
	protected FavouriteDAO favouriteDAO;

	public void addToFavourite(User user, Point point, String name) {
		if(user==null || point==null || name==null)
			throw new IllegalArgumentException("User, point or name cannot be null");
		Favourite favourite = new Favourite(point,name,user);
		favouriteDAO.add(favourite);
	}

	public void removeFromFavourite(User user, String name) {
		if(user==null || name==null)
			throw new IllegalArgumentException("User or name cannot be null");
		Favourite favourite = favouriteDAO.findByUserAndName(user, name);
		if(favourite==null)
			throw new IllegalArgumentException("Favourite does not exist");
		favouriteDAO.delete(favourite);
	}

	public List<Favourite> getFavourites(User user) {
		if(user==null)
			throw new IllegalArgumentException("User cannot be null");
		return favouriteDAO.findByUser(user);
	}

}
