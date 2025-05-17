package rsa.quad;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Trie with 4 sub tries with equal dimensions covering all its area.
 * This class corresponds to the Container in the Composite design pattern.
 */
class NodeTrie<T extends HasPoint> extends Trie<T> {

    final Map<Quadrant, Trie<T>> tries;

    /**
     * Create a node in given rectangle.
     * @param topLeftX of rectangle
     * @param topLeftY of rectangle
     * @param bottomRightX of rectangle
     * @param bottomRightY of rectangle
     */
    NodeTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        super(topLeftX, topLeftY, bottomRightX, bottomRightY);
        tries = new HashMap<>();
    }

    /**
     * Quadrant of a point in this node.
     * @param point to compute quadrant.
     * @return quadrant
     */
    Quadrant quadrantOf(T point) {
        return quadrantOf(point.x(), point.y());
    }

    /**
     * Quadrant of a point in this node.
     * @param x coordinate of point compute quadrant.
     * @param y coordinate of point compute quadrant.
     * @return quadrant
     */
    Quadrant quadrantOf(double x, double y) {
        double midX = (topLeftX + bottomRightX) / 2;
        double midY = (topLeftY + bottomRightY) / 2;

        if (x <= midX && y >= midY) {
            return Quadrant.NW;
        } else if (x >= midX && y >= midY) {
            return Quadrant.NE;
        } else if (x <= midX && y <= midY) {
            return Quadrant.SW;
        } else {
            return Quadrant.SE;
        }
    }

    /**
     * A collection of tries that descend from this one
     * @return collection of tries
     */
    Collection<Trie<T>> getTries() {
        return tries.values();
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Find a recorded point with the same coordinates of given point
     * @param point with requested coordinates
     * @return recorded point, if found; null otherwise
     */
    @Override
    T find(T point) {
        Quadrant quadrant = quadrantOf(point);
        Trie<T> childTrie = tries.get(quadrant);
        return childTrie == null ? null : childTrie.find(point);
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Insert given point
     * @param point to be inserted
     * @return changed parent node
     */
    @Override
    Trie<T> insert(T point) {
        Quadrant quadrant = quadrantOf(point);
        Trie<T> childTrie = tries.get(quadrant);

        if (childTrie == null) {
            double midX = (topLeftX + bottomRightX) / 2;
            double midY = (topLeftY + bottomRightY) / 2;

            childTrie = switch (quadrant) {
                case NW -> new LeafTrie<>(topLeftX, topLeftY, midX, midY);
                case NE -> new LeafTrie<>(midX, topLeftY, bottomRightX, midY);
                case SW -> new LeafTrie<>(topLeftX, midY, midX, bottomRightY);
                case SE -> new LeafTrie<>(midX, midY, bottomRightX, bottomRightY);
            };

            tries.put(quadrant, childTrie);
        }

        Trie<T> result = childTrie.insert(point);

        if (result != childTrie) {
            tries.put(quadrant, result);
        }

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
        Quadrant quadrant = quadrantOf(point);
        return tries.get(quadrant).insertReplace(point);
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
        Collection<Trie<T>> listTries = getTries();
        for (Trie<T> trie : listTries) {
            if (trie.overlaps(x, y, radius)) {
                trie.collectNear(x, y, radius, points);
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
        for (Quadrant quadrant : tries.keySet()) {
            tries.get(quadrant).collectAll(points);
        }
    }

    /**
     * Description copied from class: {@link Trie}
     * <p>Delete given point
     * @param point to delete
     */
    @Override
    void delete(T point) {
        Quadrant quadrant = quadrantOf(point);
        tries.get(quadrant).delete(point);
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
        return "NodeTrie{" +
                "tries=" + tries +
                ", bottomRightX=" + bottomRightX +
                ", bottomRightY=" + bottomRightY +
                ", topLeftX=" + topLeftX +
                ", topLeftY=" + topLeftY +
                '}';
    }
}
