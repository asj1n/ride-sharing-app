package org.vaadin.rsa.ride;

import org.vaadin.rsa.match.RideMatch;

import java.util.Comparator;

/**
 * A type proving a comparator of RideMatchInfo instances.
 * This is part of the abstract component of the Factory Method design pattern.
 */
public interface RideMatchSorter {

    /**
     * Provides a comparator of RideMatch instances.
     * @return comparator
     */
    Comparator<RideMatch> getComparator();
}
