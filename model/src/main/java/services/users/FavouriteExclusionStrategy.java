package services.users;


import model.Favourite;
import model.User;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class FavouriteExclusionStrategy implements ExclusionStrategy {

	    public boolean shouldSkipClass(Class<?> arg0) {
	        return arg0 == User.class || arg0 == GeometryFactory.class;
	    }

	    public boolean shouldSkipField(FieldAttributes f) {

	        return (f.getDeclaringClass() == Favourite.class && f.getName().equals("id"))||
	        		(f.getDeclaringClass() == Geometry.class && f.getName().equals("SRID"));
	    }
	    

	
}
