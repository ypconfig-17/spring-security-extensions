package io.github.ypconfig17.security.core.exception;

/**
 * @author yangpan
 */
public class NotImplementedException extends UnsupportedOperationException {

    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }
}
