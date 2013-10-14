package services.shapefiles;

import javax.ejb.Local;

@Local
public interface ShapeLoader {

	public void readShp(String url);
	
}
