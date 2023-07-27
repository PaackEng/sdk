package eu.paack.sdk.exceptions;


public class PaackInitFailedException extends Exception {

    public PaackInitFailedException(String message) {
        super(message);
    }

    public PaackInitFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
