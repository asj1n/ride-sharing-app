package org.vaadin.rsa.quad;

/**
 * The Visitor interface, part of the abstract layer of the design pattern with the same name.
 *
 * <p>In this case, the visitor is parameterized by {@link HasPoint}HasPoint and defines visit methods
 * for each of the types in the composite, namely {@link LeafTrie} and {@link NodeTrie}.
 */
public interface Visitor<T extends HasPoint> {

    /**
     * Do a visit to a node in the composite structure
     * @param node to be visited
     */
    void visit(NodeTrie<T> node);

    /**
     * Do a visit to a leaf in the composite structure
     * @param leaf to be visited
     */
    void visit(LeafTrie<T> leaf);
}
