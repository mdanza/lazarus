package services.authentication.encryption.exception;

public class InvalidTokenException extends Exception {

	public InvalidTokenException() {
		super();
	}

	public InvalidTokenException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 4861815566564064893L;
}
