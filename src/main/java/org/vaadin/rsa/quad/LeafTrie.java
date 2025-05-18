package org.vaadin.rsa.quad;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Trie that has no descendants. This class corresponds to the Leaf in the Composite design pattern.
 */
class LeafTrie<T extends HasPoint> extends Trie<T> {

    final Set<T> points;

    /**
     * Create a leaf in given rectangle
     * @param topLeftX of rectangle
     * @param topLeftY of rectangle
     * @param bottomRightX of rectangle
     * @param bottomRightY of rectangle
     */
    LeafTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        super(topLeftX, topLeftY, bottomRightX, bottomRightY);
        points = new HashSet<>();
    }

    /**
     * A collection of points currently in this leaf
     * @return collection of points
     */
    Collection<T> getPoints() {
        return points;
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    @Override
    T find(T point) {
        for (T pointInSet : getPoints()) {
            if (pointInSet.x() == point.x() && pointInSet.y() == point.y()) {
                return pointInSet;
            }
        }

        return null;
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Insert given point
     * @param point to be inserted
     * @return changed parent node
     */
    @Override
    Trie<T> insert(T point) {
        if (points.size() == capacity) {
            Set<T> clonePoints = new HashSet<>(points);
            points.clear();
            NodeTrie<T> nodeTrie = new NodeTrie<>(topLeftX, topLeftY, bottomRightX, bottomRightY);

            for (T pointInSet : clonePoints) {
                nodeTrie.insert(pointInSet);
            }

            nodeTrie.insert(point);
            return nodeTrie;
        }

        points.add(point);
        return this;
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Insert given point, replacing existing points in same location
     * @param point point to be inserted
     * @return changed parent node
     */
    @Override
    Trie<T> insertReplace(T point) {
        T pointInSameLocation = find(point);

        if (pointInSameLocation != null) {
            points.remove(pointInSameLocation);
        }

        return insert(point);
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Collect points at a distance smaller or equal to radius from (x,y) and place them in given list
     * @param x coordinate of point
     * @param y coordinate of point
     * @param radius from given point
     * @param points set for collecting points
     */
    @Override
    void collectNear(double x, double y, double radius, Set<T> points) {
        for (T point : getPoints()) {
            if (getDistance(point.x(), point.y(), x, y) <= radius) {
                points.add(point);
            }
        }
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Collect all points in this node and its descendants in given set
     * @param points set of {@link HasPoint} for collecting points
     */
    @Override
    void collectAll(Set<T> points) {
        points.addAll(getPoints());
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Delete given point
     * @param point to delete
     */
    @Override
    void delete(T point) {
        points.remove(point);
    }

    /**
     * Description copied from interface: {@link Element}
     * <p>Accept a visitor to operate on a node of the composite structure
     * @param visitor to the node
     */
    @Override
    public void accept(Visitor<T> visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LeafTrie{" +
                "points=" + points +
                ", topLeftY=" + topLeftY +
                ", topLeftX=" + topLeftX +
                ", bottomRightY=" + bottomRightY +
                ", bottomRightX=" + bottomRightX +
                '}';
    }
}
