package org.vaadin.rsa;

/**
 * An exception raised by the ride-sharing app.
 */
public class RideSharingAppException extends Exception {

    /**
     * Instances a basic exception without details.
     */
    public RideSharingAppException() {
        super();
    }

    /**
     * Instances an exception with message.
     * @param message explaining exception.
     */
    public RideSharingAppException(String message) {
        super(message);
    }

    /**
     * Instances an exception with message and cause.
     * @param cause of exception (can be null).
     */
    public RideSharingAppException(Throwable cause) {
        super(cause);
    }

    /**
     * Instances an exception with message and cause.
     * @param message explaining exception.
     * @param cause of exception (can be null).
     */
    public RideSharingAppException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instances an exception with message, cause and control of suppression and stack trace writing.
     * @param message explaining exception.
     * @param cause of exception (can be null).
     * @param enableSuppression true to enable suppression; false otherwise.
     * @param writableStackTrace true to write on stack trace; false otherwise.
     */
    public RideSharingAppException(String message, Throwable cause,
                                   boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
