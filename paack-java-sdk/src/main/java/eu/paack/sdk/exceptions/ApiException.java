package eu.paack.sdk.exceptions;

public class ApiException extends Exception {

    public ApiException(String message, Throwable e) {
        super(message, e);
    }

    public ApiException(Throwable throwable) {
        super(throwable);
    }

    public ApiException(String message) {
        super(message);
    }
}
