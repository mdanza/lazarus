package services.address;

import model.Corner;
import model.Street;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CloseLocationDataExclusionStrategy implements ExclusionStrategy{
	
	    public boolean shouldSkipClass(Class<?> arg0) {
	        return false;
	    }

	    public boolean shouldSkipField(FieldAttributes f) {
	        return (f.getDeclaringClass() == Street.class && f.getName().equals("id"))||
	        		(f.getDeclaringClass() == Street.class && f.getName().equals("nameCode"))||
	        		(f.getDeclaringClass() == Street.class && f.getName().equals("segments"))||
	        		(f.getDeclaringClass() == Corner.class && f.getName().equals("id"))||
	        		(f.getDeclaringClass() == Corner.class && f.getName().equals("point"))||
	        		(f.getDeclaringClass() == Corner.class && f.getName().equals("firstStreetNameCode"))||
	        		(f.getDeclaringClass() == Corner.class && f.getName().equals("secondStreetNameCode"));
	    }
	    


}
