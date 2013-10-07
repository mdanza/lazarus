package services.authentication.security;

import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.Test;

public class PasswordHashTest {

	@Test
	public void hashTest() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		String password = "hola1234";
		byte[] salt = PasswordHash.generateRandomSalt();
		int iterations = PasswordHash.getIterationCount();

		String hash = PasswordHash.createHash(password, salt, iterations);

		assertTrue(PasswordHash.validatePassword(password, hash,
				PasswordHash.toHex(salt), iterations));
	}

	@Test
	public void hexConversion() {
		byte[] generated = PasswordHash.generateRandomSalt();
		String converted = PasswordHash.toHex(generated);
		byte[] reConverted = PasswordHash.fromHex(converted);
		assertTrue(PasswordHash.slowEquals(generated, reConverted));
	}
}
