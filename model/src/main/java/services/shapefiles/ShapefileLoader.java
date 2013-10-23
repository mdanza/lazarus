package services.shapefiles;

import javax.ejb.Local;

@Local
public interface ShapefileLoader {

	public void readShp(String url);
	
}
