package services.shapefiles;

import javax.ejb.EJB;

import model.ShapefileWKT;
import model.dao.ShapefileWKTDAO;

public class ShapefileStatusServiceImpl implements ShapefileStatusService {

	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;

	@Override
	public ShapefileStatus getUploadStatus() {
		ShapefileWKT addressWKT = shapefileWKTDAO.find(ShapefileWKT.ADDRESS);
		ShapefileWKT busMaximalWKT = shapefileWKTDAO
				.find(ShapefileWKT.BUS_MAXIMAL);
		ShapefileWKT busStopWKT = shapefileWKTDAO.find(ShapefileWKT.BUS_STOP);
		ShapefileWKT controlPointWKT = shapefileWKTDAO
				.find(ShapefileWKT.CONTROL_POINT);
		ShapefileWKT cornerWKT = shapefileWKTDAO.find(ShapefileWKT.CORNER);
		ShapefileWKT streetWKT = shapefileWKTDAO.find(ShapefileWKT.STREET);
		ShapefileStatus status = new ShapefileStatus();
		status.setAddressesUploadPercentage(addressWKT != null ? addressWKT
				.getProgress() : 0);
		status.setBusRoutesMaximalUploadPercentage(busMaximalWKT != null ? busMaximalWKT
				.getProgress() : 0);
		status.setBusStopsUploadPercentage(busStopWKT != null ? busStopWKT
				.getProgress() : 0);
		status.setControlPointsUploadPercentage(controlPointWKT != null ? controlPointWKT
				.getProgress() : 0);
		status.setCornersUploadPercentage(cornerWKT != null ? cornerWKT
				.getProgress() : 0);
		status.setStreetsUploadPercentage(streetWKT != null ? streetWKT
				.getProgress() : 0);
		return status;
	}

}
