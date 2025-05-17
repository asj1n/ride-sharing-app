package rsa.quad;

import java.util.*;

/**
 * This class follows the Facade design pattern and presents a single access point to manage quad trees.
 * It provides methods for inserting, deleting and finding elements implementing {@link HasPoint}.
 * This class corresponds to the Client in the Composite design pattern used in this package.
 */
public class PointQuadtree<T extends HasPoint> implements Iterable<T> {

    Trie<T> top;

    /**
     * Create a quad tree for points in a rectangle with given top left and bottom right corners.
     * This is typically used for a region in the Cartesian plane, as used in mathematics,
     * and can also be used for geographic coordinates.
     * @param topLeftX x coordinate of top left corner
     * @param topLeftY y coordinate of top left corner
     * @param bottomRightX x coordinate of bottom right corner
     * @param bottomRightY y coordinate of bottom right corner
     */
    public PointQuadtree(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        top = new LeafTrie<>(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }

    /**
     * Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    public T find(T point) {
        return top.find(point);
    }

    /**
     * Checks if a point is within the quadtree boundaries
     * @param point to check
     * @return true if point is within boundaries, false otherwise
     */
    private boolean inBoundaries(T point) {
        return top.topLeftX <= point.x() && point.x() <= top.bottomRightX &&
               top.bottomRightY <= point.y() && point.y() <= top.topLeftY;
    }

    /**
     * Insert given point in the QuadTree
     * @param point to be inserted
     */
    public void insert(T point) {
        if (!inBoundaries(point)) {
            throw new PointOutOfBoundException();
        }
        top = top.insert(point);
    }

    /**
     * Insert point, replacing existing point in the same position
     * @param point point to be inserted
     */
    public void insertReplace(T point) {
        if (!inBoundaries(point)) {
            throw new PointOutOfBoundException();
        }
        top = top.insertReplace(point);
    }

    /**
     * Returns a set of points at a distance smaller or equal to radius from point with given coordinates.
     * @param x coordinate of point
     * @param y coordinate of point
     * @param radius from given point
     * @return set of instances of type {@link HasPoint}
     */
    public Set<T> findNear(double x, double y, double radius) {
        Set<T> near = new HashSet<>();
        top.collectNear(x, y, radius, near);
        return near;
    }

    /**
     * A set with all points in the QuadTree
     * @return set of instances of type {@link HasPoint}
     */
    public Set<T> getAll() {
        Set<T> all = new HashSet<>();
        top.collectAll(all);
        return all;
    }

    /**
     * Delete given point from QuadTree, if it exists there
     * @param point to be deleted
     */
    public void delete(T point) {
        top.delete(point);
    }

    /**
     * Returns an iterator over the points stored in the quad tree
     * @return iterator in interface Iterable<T extends HasPoint>
     */
    @Override
    public Iterator<T> iterator() {
        return new PointIterator();
    }

    /**
     * Iterator over points stored in the internal node structure It traverses the tree depth first,
     * using coroutine with threads, and collects all points in no particular order.
     * An instance of this class is returned by {@link PointQuadtree#iterator()}
     */
    public class PointIterator implements Iterator<T>, Runnable, Visitor<T> {

        private Iterator<T> iterator;
        private final List<T> allPoints = new ArrayList<>();

        /**
         * Creates a Point iterator.
         */
        PointIterator() {
            run();
        }

        @Override
        public void run() {
            top.accept(this);
            iterator = allPoints.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next();
        }

        /**
         * Description copied from interface: {@link Visitor}
         * <p>Do a visit to a node in the composite structure
         * @param node to be visited
         */
        @Override
        public void visit(NodeTrie<T> node) {
            for (Trie<T> childTrie: node.getTries()) {
                childTrie.accept(this);
            }
        }

        /**
         * Description copied from interface: {@link Visitor}
         * <p>Do a visit to a leaf in the composite structure
         * @param leaf to be visited
         */
        @Override
        public void visit(LeafTrie<T> leaf) {
            allPoints.addAll(leaf.getPoints());
        }
    }
}
