package services.authentication;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.User;
import model.dao.UserDAO;

import org.apache.log4j.Logger;

import services.authentication.encryption.exception.InvalidTokenException;
import services.authentication.security.PasswordHash;
import services.authentication.token.Token;

@Stateless(name = "AuthenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

	private static Logger logger = Logger
			.getLogger(AuthenticationServiceImpl.class);
	private int duration = 6000;

	@EJB(beanName = "UserDAO")
	protected UserDAO userDAO;

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
				logger.info("validate token: token expired");
				throw new IllegalArgumentException("invalid token");
			}
			String username = aToken.getUsername();
			User user = userDAO.find(username);
			if (user != null) {
				if (user.getPassword() != null
						&& user.getPassword().equals(aToken.getPassword())) {
					return user;
				} else {
					throw new IllegalArgumentException("corrupt token");
				}
			} else {
				throw new IllegalArgumentException("invalid token null user");
			}
		} catch (InvalidTokenException e) {
			logger.info("authenticate: token invalid");
			throw new IllegalArgumentException("invalid token");
		}
	}

	public String authenticate(String username, String password) {
		logger.info("authenticate ran with user " + username);
		User storedUser = userDAO.find(username);
		if (storedUser == null)
			throw new IllegalArgumentException("User does not exist");
		if (!storedUser.isActive())
			throw new IllegalArgumentException("User is not active");
		else {
			String hashedPassword = storedUser.getPassword();
			String savedSalt = storedUser.getSalt();
			int iterations = storedUser.getCryptographicIterations();
			try {
				if (PasswordHash.validatePassword(password, hashedPassword,
						savedSalt, iterations))
					return (new Token(storedUser.getUsername(),
							storedUser.getPassword())).toString();
				else {
					logger.info("authenticate user " + username
							+ " wrong password");
					throw new IllegalArgumentException("wrong password");
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
