package org.vaadin.rsa.quad;

/**
 * The Element interface, part of the abstract layer of the Visitor design pattern.
 * <p>In this case, the elements is parameterized by {@link HasPoint} and defines a method to accept a {@link Visitor}.
 * This type must be added to the Component ({@link Trie}) of the Composite
 * to ensure that all types of the structure implement it.
 */
public interface Element<T extends HasPoint> {

    /**
     * Accept a visitor to operate on a node of the composite structure
     * @param visitor to the node
     */
    void accept(Visitor<T> visitor);
}
