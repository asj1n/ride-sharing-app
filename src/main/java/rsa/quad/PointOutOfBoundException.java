package rsa.quad;

/**
 * Exception raised when the quad tree is used with a point outside its boundaries.
 * Programmers can easily avoid these exceptions by checking points before attempting
 * to insert them in a quad tree. Since it extends {@link RuntimeException},
 * it is not mandatory to handle this kind of exception.
 */
public class PointOutOfBoundException extends RuntimeException {

    /**
     * Create an exception without arguments
     */
    public PointOutOfBoundException() {
        super();
    }
}
