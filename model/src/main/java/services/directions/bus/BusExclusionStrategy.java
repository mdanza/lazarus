package services.directions.bus;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import model.Obstacle;

public class BusExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == Obstacle.class && f.getName().equals("id"))||
                        (f.getDeclaringClass() == Obstacle.class && f.getName().equals("circle"))||
                        (f.getDeclaringClass() == Obstacle.class && f.getName().equals("centre"))||
                        (f.getDeclaringClass() == Obstacle.class && f.getName().equals("user"))||
                        (f.getDeclaringClass() == Obstacle.class && f.getName().equals("createdAt"));
    }
    

}
