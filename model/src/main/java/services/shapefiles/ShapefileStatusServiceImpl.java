package services.shapefiles;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import model.ShapefileWKT;
import model.dao.ShapefileWKTDAO;

@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Singleton(name = "ShapefileStatusService")
public class ShapefileStatusServiceImpl implements ShapefileStatusService {
	private ShapefileStatus status;
	
	@EJB(name = "ShapefileWKTDAO")
	private ShapefileWKTDAO shapefileWKTDAO;

	@PostConstruct
    public void applicationStartup() {
		ShapefileWKT addressWKT = shapefileWKTDAO.find(ShapefileWKT.ADDRESS);
		ShapefileWKT busMaximalWKT = shapefileWKTDAO
				.find(ShapefileWKT.BUS_MAXIMAL);
		ShapefileWKT busStopWKT = shapefileWKTDAO.find(ShapefileWKT.BUS_STOP);
		ShapefileWKT controlPointWKT = shapefileWKTDAO
				.find(ShapefileWKT.CONTROL_POINT);
		ShapefileWKT cornerWKT = shapefileWKTDAO.find(ShapefileWKT.CORNER);
		ShapefileWKT streetWKT = shapefileWKTDAO.find(ShapefileWKT.STREET);
		status = new ShapefileStatus();
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
    }
	
	@Override
	public void setStreetsUploadProgress(double progress){
		status.setStreetsUploadPercentage(progress);
	}
	@Override
	public void setCornersUploadProgress(double progress){
		status.setCornersUploadPercentage(progress);
	}
	
	@Override
	public void setControlPointsUploadProgress(double progress){
		status.setControlPointsUploadPercentage(progress);
	}
	@Override
	public void setBusStopsUploadProgress(double progress) {
		status.setBusStopsUploadPercentage(progress);
	}
	
	@Override
	public void setAddressUploadProgress(double progress){
		status.setAddressesUploadPercentage(progress);
	}
	
	@Override
	public void setBusRouteMaximalUploadProgress(double progress){
		status.setBusRoutesMaximalUploadPercentage(progress);
	}
	
	@Override
	public ShapefileStatus getUploadStatus() {
		return status;
	}

}
