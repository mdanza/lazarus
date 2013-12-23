package services.shapefiles;

import javax.ejb.Local;

@Local
public interface ShapefileStatusService {
	public ShapefileStatus getUploadStatus();

	public class ShapefileStatus {
		private double addressesUploadPercentage;
		private double busRoutesMaximalUploadPercentage;
		private double busStopsUploadPercentage;
		private double controlPointsUploadPercentage;
		private double cornersUploadPercentage;
		private double streetsUploadPercentage;

		public double getAddressesUploadPercentage() {
			return addressesUploadPercentage;
		}

		public void setAddressesUploadPercentage(
				double addressesUploadPercentage) {
			this.addressesUploadPercentage = addressesUploadPercentage;
		}

		public double getBusRoutesMaximalUploadPercentage() {
			return busRoutesMaximalUploadPercentage;
		}

		public void setBusRoutesMaximalUploadPercentage(
				double busRoutesMaximalUploadPercentage) {
			this.busRoutesMaximalUploadPercentage = busRoutesMaximalUploadPercentage;
		}

		public double getBusStopsUploadPercentage() {
			return busStopsUploadPercentage;
		}

		public void setBusStopsUploadPercentage(double busStopsUploadPercentage) {
			this.busStopsUploadPercentage = busStopsUploadPercentage;
		}

		public double getControlPointsUploadPercentage() {
			return controlPointsUploadPercentage;
		}

		public void setControlPointsUploadPercentage(
				double controlPointsUploadPercentage) {
			this.controlPointsUploadPercentage = controlPointsUploadPercentage;
		}

		public double getCornersUploadPercentage() {
			return cornersUploadPercentage;
		}

		public void setCornersUploadPercentage(double cornersUploadPercentage) {
			this.cornersUploadPercentage = cornersUploadPercentage;
		}

		public double getStreetsUploadPercentage() {
			return streetsUploadPercentage;
		}

		public void setStreetsUploadPercentage(double streetsUploadPercentage) {
			this.streetsUploadPercentage = streetsUploadPercentage;
		}

	}
}
