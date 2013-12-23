package services.shapefiles;

import javax.ejb.Local;

@Local
public interface ShapefileLoader {

	public void updateShp(String url);
	
}
