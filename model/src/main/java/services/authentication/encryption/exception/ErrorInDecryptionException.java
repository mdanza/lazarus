package services.authentication.encryption.exception;

public class ErrorInDecryptionException extends Exception {

	private static final long serialVersionUID = 5667459605677414425L;

	public ErrorInDecryptionException() {
        super();
    }
	public ErrorInDecryptionException(String message) {
        super(message);
    }

}
