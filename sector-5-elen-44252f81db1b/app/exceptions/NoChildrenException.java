package exceptions;

/**
 * NoChildrenException.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
public class NoChildrenException extends Exception {

    /**
     * Instantiates a new No children exception.
     */
    public NoChildrenException() {
    }

    /**
     * Instantiates a new No children exception.
     *
     * @param message the message
     */
    public NoChildrenException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new No children exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public NoChildrenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new No children exception.
     *
     * @param cause the cause
     */
    public NoChildrenException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new No children exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public NoChildrenException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
