package services.authentication.token;

import java.util.Date;

import org.apache.log4j.Logger;

import services.authentication.encryption.Encrypter;
import services.authentication.encryption.exception.ErrorInDecryptionException;
import services.authentication.encryption.exception.InvalidTokenException;

/**
 * Represents a token
 */
public class Token {

	String username;
	long createdAt;
	static Encrypter encrypter = new Encrypter();
	private static Logger logger = Logger.getLogger(Token.class);
	final static String separator = encrypter.encrypt("!~$@");

	public Token(String username, long createdAt) {
		this.username = username;
		this.createdAt = createdAt;
	}

	public Token(String username) {
		this.username = username;
		this.createdAt = (new Date()).getTime();
	}

	public String getUsername() {
		return username;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {

		String decrypted = Long.toString(this.createdAt) + separator
				+ this.username;
		return encrypter.encrypt(decrypted);
	}

	public static Token getToken(String token) throws InvalidTokenException {
		try {
			String decrypted = encrypter.decrypt(token);
			String[] pieces = decrypted.split(separator, 2);
			String s = pieces[0];
			long createdAt = Long.valueOf(s);
			String username = pieces[1];
			return new Token(username, createdAt);
		} catch (ErrorInDecryptionException e) {
			logger.warn("getToken token not valid");
			throw new InvalidTokenException();
		}
	}

}
