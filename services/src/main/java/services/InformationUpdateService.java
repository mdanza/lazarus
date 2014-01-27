package services;

import helpers.RestResultsHelper;
import helpers.ShapefileUploaderHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import model.User;
import model.User.Role;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import services.authentication.AuthenticationService;
import services.shapefiles.ShapefileLoader;
import services.shapefiles.ShapefileStatusService;
import services.shapefiles.ShapefileStatusService.ShapefileStatus;
import services.shapefiles.address.AddressLoader;
import services.shapefiles.bus.BusRoutesMaximalLoader;
import services.shapefiles.bus.BusStopLoader;
import services.shapefiles.corner.CornerLoader;
import services.shapefiles.streets.StreetLoader;
import services.streets.abbreviations.AbbreviationService;
import services.taxis.TaxiServiceService;

import com.google.gson.Gson;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Stateless(name = "InformationUpdateService")
@Path("v1/api/information")
public class InformationUpdateService {

	static Logger logger = Logger.getLogger(InformationUpdateService.class);

	private Gson gson = new Gson();

	@EJB(name = "ShapefileUploaderHelper")
	private ShapefileUploaderHelper shapefileUploaderHelper;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	@EJB(name = "ShapefileStatusService")
	private ShapefileStatusService shapefileStatusService;

	@EJB(name = "StreetLoader")
	private StreetLoader streetLoader;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "BusStopLoader")
	private BusStopLoader busStopLoader;

	@EJB(name = "BusRoutesMaximalLoader")
	private BusRoutesMaximalLoader busRoutesMaximalLoader;

	@EJB(name = "AddressLoader")
	private AddressLoader addressLoader;

	@EJB(name = "CornerLoader")
	private CornerLoader cornerLoader;

	@EJB(name = "AbbreviationService")
	private AbbreviationService abbreviationService;

	@EJB(name = "TaxiServiceService")
	private TaxiServiceService taxiServiceService;

	@POST
	@Path("/uploadStreets")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadStreets(@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return uploadShapefile(token, uploadedInputStream, fileDetail,
				streetLoader);
	}

	@POST
	@Path("/uploadAddresses")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadAddress(@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return uploadShapefile(token, uploadedInputStream, fileDetail,
				addressLoader);
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/uploadBusStops")
	public String busStops(@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return uploadShapefile(token, uploadedInputStream, fileDetail,
				busStopLoader);
	}

	// @POST
	// @Path("/uploadControlPoints")
	// @Consumes(MediaType.MULTIPART_FORM_DATA)
	// public String controlPoints(@HeaderParam("Authorization") String token,
	// @FormDataParam("file") InputStream uploadedInputStream,
	// @FormDataParam("file") FormDataContentDisposition fileDetail) {
	// return uploadShapefile(token, uploadedInputStream, fileDetail,
	// controlPointLoader);
	// }

	@POST
	@Path("/uploadBusRoutesMaximal")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String busRoutesMaximal(@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return uploadShapefile(token, uploadedInputStream, fileDetail,
				busRoutesMaximalLoader);
	}

	@POST
	@Path("/uploadCorners")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadCorners(@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return uploadShapefile(token, uploadedInputStream, fileDetail,
				cornerLoader);
	}

	@GET
	@Path("/status")
	public String getUploadStatus(@HeaderParam("Authorization") String token) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Token cannot be empty or null");
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			ShapefileStatus result = shapefileStatusService.getUploadStatus();
			return restResultsHelper.resultWrapper(true, gson.toJson(result));
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	private String uploadShapefile(String token,
			InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail, ShapefileLoader loader) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Token cannot be empty or null"));
		if (uploadedInputStream == null)
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Null file"));
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			try {
				File zippedFile = File.createTempFile(
						RandomStringUtils.randomAlphanumeric(8), ".zip");
				OutputStream out = new FileOutputStream(zippedFile);
				int read = 0;
				byte[] buffer = new byte[1024];
				while ((read = uploadedInputStream.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				out.flush();
				out.close();
				shapefileUploaderHelper.uploadShapefile(loader, zippedFile);
				return restResultsHelper.resultWrapper(true,
						gson.toJson("Successfully received shapefile"));
			} catch (IOException e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						gson.toJson("Error uploading file"));
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@POST
	@Path("/uploadStreetTitles")
	public String uploadStreetTitles(
			@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Token cannot be empty or null"));
		if (uploadedInputStream == null)
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Null file"));
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			try {
				File file = File.createTempFile(
						RandomStringUtils.randomAlphanumeric(8), ".csv");
				OutputStream out = new FileOutputStream(file);
				int read = 0;
				byte[] buffer = new byte[1024];
				while ((read = uploadedInputStream.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				out.flush();
				out.close();
				abbreviationService.saveRouteTitles(file);
				return restResultsHelper.resultWrapper(true,
						gson.toJson("Successfully received csv"));
			} catch (IOException e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						gson.toJson("Error uploading file"));
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@POST
	@Path("/uploadStreetTypes")
	public String uploadStreetTypes(@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Token cannot be empty or null"));
		if (uploadedInputStream == null)
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Null file"));
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			try {
				File file = File.createTempFile(
						RandomStringUtils.randomAlphanumeric(8), ".csv");
				OutputStream out = new FileOutputStream(file);
				int read = 0;
				byte[] buffer = new byte[1024];
				while ((read = uploadedInputStream.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				out.flush();
				out.close();
				abbreviationService.saveRouteTypes(file);
				return restResultsHelper.resultWrapper(true,
						gson.toJson("Successfully received csv"));
			} catch (IOException e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						gson.toJson("Error uploading file"));
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@POST
	@Path("/uploadTaxiServices")
	public String uploadTaxiServices(
			@HeaderParam("Authorization") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		if (token == null || token.equals(""))
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Token cannot be empty or null"));
		if (uploadedInputStream == null)
			return restResultsHelper.resultWrapper(false,
					gson.toJson("Null file"));
		try {
			User user = authenticationService.authenticate(token);
			if (!user.getRole().equals(Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			try {
				File file = File.createTempFile(
						RandomStringUtils.randomAlphanumeric(8), ".csv");
				OutputStream out = new FileOutputStream(file);
				int read = 0;
				byte[] buffer = new byte[1024];
				while ((read = uploadedInputStream.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				out.flush();
				out.close();
				taxiServiceService.uploadInfo(file);
				return restResultsHelper.resultWrapper(true,
						gson.toJson("Successfully received csv"));
			} catch (IOException e) {
				e.printStackTrace();
				return restResultsHelper.resultWrapper(false,
						gson.toJson("Error uploading file"));
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}
}
