package services;

import helpers.RestResultsHelper;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import model.User;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

import serialization.UserSerializer;
import services.authentication.AuthenticationService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Stateless(name = "UserService")
@Path("v1/api/users")
public class UserService {

	private static final String username = "lazarus4blind@gmail.com";
	private static final String password = "lazarusisfuckingawesome";

	static Logger logger = Logger.getLogger(UserService.class);

	@EJB(name = "UserDAO")
	private UserDAO userDAO;

	@EJB(name = "AuthenticationService")
	private AuthenticationService authenticationService;

	@EJB(name = "RestResultsHelper")
	private RestResultsHelper restResultsHelper;

	private Gson gson = createGson();

	private Gson createGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(User.class, new UserSerializer());
		return builder.create();
	}

	@POST
	public String register(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("email") String email) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.setActive(true);
		user.setRole(model.User.Role.USER);
		try {
			userDAO.add(user);
			return restResultsHelper.resultWrapper(true,
					"User added successfully");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "could not add user");
		}
	}

	@POST
	@Path("/login")
	public String login(@FormParam("username") String username,
			@FormParam("password") String password) {
		if (username == null || username.equals("") || password == null
				|| password.equals(""))
			return restResultsHelper.resultWrapper(false,
					"No nulls nor empty strings allowed");
		try {
			return restResultsHelper.resultWrapper(true,
					authenticationService.authenticate(username, password));
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "login failed");
		}
	}

	@DELETE
	public String delete(@HeaderParam("Authorization") String token,
			@QueryParam("username") String username) {
		if (token == null || token.equals("") || username == null
				|| username.equals(""))
			return restResultsHelper.resultWrapper(false,
					"Empty or null arguments");
		try {
			User actionUser = authenticationService.authenticate(token);
			User deleting = userDAO.find(username);
			if (actionUser.getUsername().equals(username)
					|| actionUser.getRole().equals(model.User.Role.ADMIN)) {
				deleting.setActive(false);
				try {
					userDAO.modify(deleting, deleting);
					return restResultsHelper.resultWrapper(true,
							"Successfully deactivated account");
				} catch (Exception e) {
					return restResultsHelper.resultWrapper(false,
							"Could not deactivate account");
				}
			} else
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@PUT
	public String modify(@HeaderParam("Authorization") String token,
			@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("email") String email) {
		try {
			User actionUser = authenticationService.authenticate(token);
			if (!actionUser.getUsername().equals(username)
					&& !actionUser.getRole().equals(model.User.Role.ADMIN))
				return restResultsHelper.resultWrapper(false,
						"Unauthorized access");
			User user = userDAO.find(username);
			User modifiedUser = new User();
			modifiedUser.setId(user.getId());
			modifiedUser.setUsername(username);
			modifiedUser.setPassword(password);
			modifiedUser.setEmail(email);
			modifiedUser.setActive(true);
			modifiedUser.setRole(model.User.Role.USER);
			try {
				userDAO.modify(user, modifiedUser);
				return restResultsHelper.resultWrapper(true,
						"User modified successfuly");
			} catch (Exception e) {
				return restResultsHelper.resultWrapper(false,
						"could not modify user");
			}
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "Invalid token");
		}
	}

	@GET
	public String getUser(@HeaderParam("Authorization") String token) {
		try {
			User user = authenticationService.authenticate(token);
			return restResultsHelper.resultWrapper(true, gson.toJson(user));
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false, "could not get user");
		}
	}

	@GET
	@Path("/email/{email}")
	public String emailInUse(@PathParam("email") String email) {
		try {
			User user = userDAO.findByEmail(email);
			if (user != null)
				return restResultsHelper.resultWrapper(true, "true");
			else
				return restResultsHelper.resultWrapper(true, "false");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false,
					"could not complete request");
		}
	}

	@GET
	@Path("/username/{username}")
	public String usernameInUse(@PathParam("username") String username) {
		try {
			User user = userDAO.find(username);
			if (user != null)
				return restResultsHelper.resultWrapper(true, "true");
			else
				return restResultsHelper.resultWrapper(true, "false");
		} catch (Exception e) {
			return restResultsHelper.resultWrapper(false,
					"could not complete request");
		}
	}

	@POST
	@Path("/password")
	public String resetPassword(@FormParam("username") String username) {
		User user = userDAO.find(username);
		if (user != null) {
			User modifiedUser = new User();
			modifiedUser.setId(user.getId());
			modifiedUser.setUsername(username);
			modifiedUser.setEmail(user.getEmail());
			modifiedUser.setActive(true);
			modifiedUser.setRole(user.getRole());
			String newPassword = generatePassword();
			modifiedUser.setPassword(newPassword);
			userDAO.modify(user, modifiedUser);
			sendEmail(user.getEmail(), "<h1>Estimado " + username
					+ "</h1>Su nueva contraseña es: " + newPassword,
					"Lazarus: Olvido de contraseña");
			return restResultsHelper.resultWrapper(true,
					"Sent email with new password");
		} else
			return restResultsHelper
					.resultWrapper(false, "User does not exist");
	}

	private String generatePassword() {
		return new BigInteger(130, new SecureRandom()).toString(16);
	}

	private void sendEmail(String to, String htmlContent, String subject) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(htmlContent, "text/html; charset=utf-8");
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
