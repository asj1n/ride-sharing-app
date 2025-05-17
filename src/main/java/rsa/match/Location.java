package rsa.match;

import rsa.quad.HasPoint;

/**
 * A location given by a pair of coordinates (doubles).
 * <p><b>Note:</b> all boilerplate methods in this class are automatically created
 * (e.g.  equals(), hashCode(), x() and y())
 * @param x coordinate
 * @param y coordinate
 */
public record Location(double x, double y) implements HasPoint {}