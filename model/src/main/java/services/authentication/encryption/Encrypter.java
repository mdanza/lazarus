package services.authentication.encryption;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import net.iharder.Base64;
import services.authentication.encryption.exception.ErrorInDecryptionException;

/**
 * Encrypts and decrypts Strings
 */
public class Encrypter {

	private MessageDigest digest;
	private SecretKeySpec key;
	private Cipher cipher;
	private String passPhrase = "Easy talking border blocking transport is arranged";

	public Encrypter() {
		try {
			digest = MessageDigest.getInstance("SHA");
			digest.update(passPhrase.getBytes());
			key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	/**
	 * Encrypts a String
	 * 
	 * @param clearText
	 *            the String to encrypt
	 * @return encrypted string
	 */
	public String encrypt(String clearText) {
		if (clearText != null) {
			try {
				cipher.init(Cipher.ENCRYPT_MODE, key);
				byte[] cipherText = cipher.doFinal(clearText.getBytes());
				return Base64.encodeBytes(cipherText);
			} catch (InvalidKeyException e) {
				throw new IllegalStateException(e.getMessage());
			} catch (BadPaddingException e) {
				throw new IllegalStateException(e.getMessage());
			} catch (IllegalBlockSizeException e) {
				throw new IllegalStateException(e.getMessage());
			}
		} else {
			throw new IllegalArgumentException("clearText null");
		}
	}

	/**
	 * Decrypts an encrypted string
	 * 
	 * @param cipherText
	 *            the encrypted string
	 * @return decrypted string
	 */
	public String decrypt(String cipherText) throws ErrorInDecryptionException {
		if (cipherText != null) {
			try {
				byte[] byteText = Base64.decode(cipherText);
				cipher.init(Cipher.DECRYPT_MODE, key);
				return new String(cipher.doFinal(byteText));
			} catch (InvalidKeyException e) {
				throw new ErrorInDecryptionException(e.getMessage());
			} catch (BadPaddingException e) {
				throw new ErrorInDecryptionException(e.getMessage());
			} catch (IllegalBlockSizeException e) {
				throw new ErrorInDecryptionException(e.getMessage());
			} catch (IOException e) {
				throw new ErrorInDecryptionException(e.getMessage());
			}
		} else {
			throw new IllegalArgumentException("cipherText null");
		}
	}

}
