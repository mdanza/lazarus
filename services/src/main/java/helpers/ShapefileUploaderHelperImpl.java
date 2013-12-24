package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import services.shapefiles.ShapefileLoader;

@Asynchronous
@Lock(LockType.WRITE)
@AccessTimeout(-1)
@Singleton(name = "ShapefileUploaderHelper")
@Startup
public class ShapefileUploaderHelperImpl implements ShapefileUploaderHelper {

	public void uploadShapefile(ShapefileLoader loader, File zippedFile) {
		byte[] buffer = new byte[1024];
		try {
			File shpFile = null;
			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(
					zippedFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String filename = ze.getName();
				String extension = filename.substring(
						filename.lastIndexOf(".") + 1, filename.length());
				File newFile = File.createTempFile(filename, "");
				// find .shp file inside zipped input
				if (extension != null && extension.equalsIgnoreCase("shp"))
					shpFile = newFile;
				FileOutputStream fos = new FileOutputStream(newFile);
				int read;
				while ((read = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, read);
				}
				fos.close();
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			loader.updateShp(shpFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
