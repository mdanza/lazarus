package services.incidents.obstacles;

import model.Obstacle;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ObstacleExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return ((f.getDeclaringClass() == Obstacle.class && f.getName().equals("circle"))||
        		(f.getDeclaringClass() == Obstacle.class && f.getName().equals("user"))||
        		(f.getName().equals("factory"))||
        		(f.getName().equals("SRID")));
    }
    

}
