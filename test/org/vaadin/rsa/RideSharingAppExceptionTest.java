package rsa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link RideSharingAppException}
 */
class RideSharingAppExceptionTest extends TestData {

    RideSharingAppException exception;

    /**
     * Ensure an exception can be thrown without arguments.
     */
    @Test
    void constructor() {
        exception = assertThrows(RideSharingAppException.class,
                () -> {
                    throw new RideSharingAppException();
                });
    }

    /**
     * Ensure that an exception can be instanced with a message
     */
    @ParameterizedTest
    @MethodSource("nameProvider")
    void constructorString(String name) {

        exception = assertThrows(RideSharingAppException.class,
                () -> {
                    throw new RideSharingAppException(name);
                });

        assertEquals(name, exception.getMessage(), "same message expected (invoked super?)");
    }

    /**
     * Ensure that an exception can be instanced with a cause (throwable)
     */
    @Test
    void constructorThrowable() {
        Throwable throwable = new Throwable();

        exception = assertThrows(RideSharingAppException.class,
                () -> {
                    throw new RideSharingAppException(throwable);
                });

        assertEquals(throwable, exception.getCause(), "cause of exception expected (invoked super?)");
    }

    /**
     * Ensure that an exception can be instanced with a message and cause (throwable)
     */
    @ParameterizedTest
    @MethodSource("nameProvider")
    void constructorStringThrowable(String name) {
        Throwable throwable = new Throwable();

        exception = assertThrows(RideSharingAppException.class,
                () -> {
                    throw new RideSharingAppException(name, throwable);
                });

        assertEquals(name, exception.getMessage(), "same message expected (invoked super?)");
        assertEquals(throwable, exception.getCause(), "cause of exception expected (invoked super?)");
    }

    static final boolean[] BOOLEANS = new boolean[]{true, false};

    static Stream<Arguments> exceptionArgumentsProvider() {
        Stream.Builder<Arguments> builder = Stream.builder();

        for (String name : NAMES) {
            for (boolean enableSuppression : BOOLEANS) {
                for (boolean disableSuppression : BOOLEANS) {
                    builder.add(Arguments.of(name, enableSuppression, disableSuppression));
                }
            }
        }

        return builder.build();
    }

    /**
     * Ensure that an exception can be instanced with a message and cause (throwable),
     * and enableSuppression and writableStack flags
     */
    @ParameterizedTest
    @MethodSource("exceptionArgumentsProvider")
    void constructorStringThrowableBooleanBoolean(String name, boolean enableSuppression, boolean writableStackTrace) {
        Throwable throwable = new Throwable();

        exception = assertThrows(RideSharingAppException.class,
                () -> {
                     Exception error = new RideSharingAppException(name, throwable, enableSuppression, writableStackTrace);
                     error.addSuppressed(new Throwable());
                     throw error;
                });


        assertEquals(name, exception.getMessage(), "same message expected (invoked super?)");
        assertEquals(throwable, exception.getCause(), "cause of exception expected (invoked super?)");
        assertEquals(enableSuppression, exception.getSuppressed().length > 0,
                "invalid count of suppressed exceptions (invoked super?)");
        assertEquals(writableStackTrace, exception.getStackTrace().length > 0,
                "invalid stack trace size (invoked super?)");
    }
}