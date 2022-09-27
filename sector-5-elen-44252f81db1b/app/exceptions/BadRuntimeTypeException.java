package exceptions;

/**
 * BadRuntimeTypeException.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
public class BadRuntimeTypeException extends RuntimeException {
    /**
     * Instantiates a new Bad runtime type exception.
     */
    public BadRuntimeTypeException() {
    }

    /**
     * Instantiates a new Bad runtime type exception.
     *
     * @param message the message
     */
    public BadRuntimeTypeException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Bad runtime type exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public BadRuntimeTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Bad runtime type exception.
     *
     * @param cause the cause
     */
    public BadRuntimeTypeException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Bad runtime type exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public BadRuntimeTypeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
