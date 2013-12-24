package helpers;

import java.io.File;

import javax.ejb.Local;

import services.shapefiles.ShapefileLoader;

@Local
public interface ShapefileUploaderHelper {

	public void uploadShapefile(ShapefileLoader loader, File zippedFile);

}
