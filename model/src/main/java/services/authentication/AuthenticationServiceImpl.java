package services.authentication;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Role;
import model.User;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

import services.authentication.encryption.Encrypter;
import services.authentication.encryption.exception.InvalidTokenException;
import services.authentication.token.Token;

@Stateless(name = "AuthenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

	private static Logger logger = Logger
			.getLogger(AuthenticationServiceImpl.class);
	private Encrypter encrypter;
	private int duration = 6000;

	@EJB(beanName = "UserDAO")
	protected UserDAO userDAO;

	public AuthenticationServiceImpl() {
		this.encrypter = new Encrypter();
	}

	/**
	 * sets the default token duration in seconds
	 * 
	 * @param duration
	 *            of the tokens in seconds
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public User authenticate(String token) {
		if (token == null) {
			logger.error("authenticate token is null");
			throw new IllegalArgumentException("null token");
		}
		try {
			Token aToken = Token.getToken(token);
			Date date = new Date();
			long now = date.getTime();
			long createdAt = aToken.getCreatedAt();
			long difference = now - createdAt;
			long maxDifference = duration * 1000;
			if (difference > maxDifference) {
				logger.trace("validate token: token expired");
				throw new IllegalArgumentException("invalid token");
			}
			String username = aToken.getUsername();
			User user = userDAO.find(username);
			return user;

		} catch (InvalidTokenException e) {
			logger.trace("authenticate: token invalid");
			throw new IllegalArgumentException("invalid token");
		}
	}

	public String authenticate(User user) {
		if (user == null) {
			logger.error("authenticate ran with user null");
			throw new IllegalArgumentException(
					"authenticate ran with user null");
		}
		logger.trace("authenticate ran with user " + user.getUsername());
		// TODO que pasa si userDAO.find no encuentra nada?
		User storedUser = userDAO.find(user.getUsername());
		if (storedUser != null
				&& storedUser.getUsername().equals(user.getUsername())
				&& storedUser.getPassword().equals(user.getPassword())) {
			return (new Token(storedUser.getEmail(), storedUser.getPassword()))
					.toString();
		} else {
			logger.trace("authenticate user " + user.getUsername()
					+ " wrong password");
			throw new IllegalArgumentException("wrong password");
		}

	}

}
