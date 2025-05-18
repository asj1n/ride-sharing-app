package org.vaadin.rsa.quad;

/**
 * An object with x and y coordinates, each with its own getter.
 */
public interface HasPoint {

    /**
     * Point's X coordinate.
     * @return x coordinate
     */
    double x();

    /**
     * Point's Y coordinate.
     * @return y coordinate
     */
    double y();
}
