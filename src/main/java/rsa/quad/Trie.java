package rsa.quad;

import java.util.Set;

/**
 * Abstract class common to all classes implementing the trie structure.
 * Defines methods required by those classes and provides general methods
 * for checking overlaps and computing distances.
 * This class corresponds to the Component in the Composite design pattern.
 */
public abstract class Trie<T extends HasPoint> implements Element<T> {

    /**
     * Quadrants of NodeTries. Names are from the compass NE = North East, etc.
     * <p><b>Note:</b> values() and valueOf() are methods available in all enumerations and don't need to be implemented.
     */
    static enum Quadrant {
        NE,  // NorthEast, upper right corner
        NW,  // NorthWest, upper left corner
        SE,  // SouthEast, Bottom right corner
        SW   // SouthWest, Bottom left corner
    }

    protected final double bottomRightX;
    protected final double bottomRightY;
    protected final double topLeftX;
    protected final double topLeftY;
    static int capacity = 10;

    /**
     * Create an instance from the top left and right bottom points' coordinates.
     * @param topLeftX x coordinate of top left corner
     * @param topLeftY y coordinate of top left corner
     * @param bottomRightX x coordinate of bottom right corner
     * @param bottomRightY y coordinate of bottom right corner
     */
    protected Trie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.bottomRightX = bottomRightX;
        this.bottomRightY = bottomRightY;
    }

    /**
     * Get capacity of a bucket
     * @return capacity
     */
    public static int getCapacity() {
        return capacity;
    }

    /**
     * Set capacity of a bucket
     * @param capacity of bucket
     */
    public static void setCapacity(int capacity) {
        Trie.capacity = capacity;
    }

    /**
     * Euclidean distance between two pair of coordinates of two points
     * @param x1 x coordinate of first point
     * @param y1 y coordinate of first point
     * @param x2 x coordinate of second point
     * @param y2 y coordinate of first point
     * @return distance between given points
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Check if overlaps with given circle
     * @param x coordinate of circle
     * @param y coordinate of circle
     * @param radius of circle
     * @return true if overlaps and false otherwise
     */
    boolean overlaps(double x, double y, double radius) {

        boolean aux = topLeftX <= x && x <= bottomRightX &&
                bottomRightY <= y && y <= topLeftY;

        return aux ||
                bottomRightX >= x - radius ||
                topLeftX <= x + radius ||
                bottomRightY <= y + radius ||
                topLeftY >= y - radius ||
                getDistance(x, y, topLeftX, topLeftY) <= radius ||
                getDistance(x, y, bottomRightX, bottomRightY) <= radius ||
                getDistance(x, y, topLeftX, bottomRightY) <= radius ||
                getDistance(x, y, bottomRightX, topLeftY) <= radius;
    }

    /**
     * Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    abstract T find(T point);

    /**
     * Insert given point
     * @param point to be inserted
     * @return changed parent node
     */
    abstract Trie<T> insert(T point);

    /**
     * Insert given point, replacing existing points in same location
     * @param point point to be inserted
     * @return changed parent node
     */
    abstract Trie<T> insertReplace(T point);

    /**
     * Collect points at a distance smaller or equal to radius from (x,y) and place them in given list
     * @param x coordinate of point
     * @param y coordinate of point
     * @param radius from given point
     * @param points set for collecting points
     */
    abstract void collectNear(double x, double y, double radius, Set<T> points);

    /**
     * Collect all points in this node and its descendants in given set
     * @param points set of {@link HasPoint} for collecting points
     */
    abstract void collectAll(Set<T> points);

    /**
     * Delete given point
     * @param point to delete
     */
    abstract void delete(T point);

    @Override
    public String toString() {
        return "Trie{" +
                "bottomRightX=" + bottomRightX +
                ", bottomRightY=" + bottomRightY +
                ", topLeftX=" + topLeftX +
                ", topLeftY=" + topLeftY +
                '}';
    }
}
