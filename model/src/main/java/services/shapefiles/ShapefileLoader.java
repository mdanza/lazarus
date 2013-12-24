package services.shapefiles;

import java.io.File;

import javax.ejb.Local;

@Local
public interface ShapefileLoader {

	public void updateShp(File file);

}
