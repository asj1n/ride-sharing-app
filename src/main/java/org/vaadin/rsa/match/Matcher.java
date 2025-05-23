package org.vaadin.rsa.match;

import org.vaadin.rsa.quad.PointQuadtree;
import org.vaadin.rsa.ride.Ride;
import org.vaadin.rsa.user.User;
import org.vaadin.rsa.user.UserStars;

import java.io.Serializable;
import java.util.*;

/**
 * A matcher of nearby driver and passenger rides. An instance of this class will match a pair of rides that:
 * <ul>
 *   <li>are not yet matched</li>
 *   <li>are currently in the same position (within a radius)</li>
 *   <li>have the same destination</li>
 *   <li>have complementary roles (driver and passenger)</li>
 * </ul>
 * Matching occurs when rides positions are updated and use quad trees {@link org.vaadin.rsa.quad}
 * to locate matches currently in nearby locations.
 */
public class Matcher implements Serializable {

    private final PointQuadtree<Ride> quadtree;
    private static Location topLeft = new Location(-1000, 1000);
    private static Location bottomRight = new Location(1000, -1000);
    private static double radius = 10;

    private final Map<Long, Ride> rides = new HashMap<>();
    private final Map<Long, RideMatch> rideMatches = new HashMap<>();

    /**
     * Constructs a ride matcher.
     */
    public Matcher() {
        quadtree = new PointQuadtree<>(topLeft.x(), topLeft.y(), bottomRight.x(), bottomRight.y());
    }

    /**
     * Location of top left corner of matching region
     * @return the topLeft
     */
    public static Location getTopLeft() {
        return topLeft;
    }

    /**
     * Change location of top left corner of matching region
     * @param topLeft location to set
     */
    public static void setTopLeft(Location topLeft) {
        Matcher.topLeft = topLeft;
    }

    /**
     * Location of bottom right corner of matching region
     * @return the bottomRight
     */
    public static Location getBottomRight() {
        return bottomRight;
    }

    /**
     * Change location of bottom right corner of matching region
     * @param bottomRight to set
     */
    public static void setBottomRight(Location bottomRight) {
        Matcher.bottomRight = bottomRight;
    }

    /**
     * Maximum distance between two users eligible for a match
     * @return the radius
     */
    public static double getRadius() {
        return radius;
    }

    /**
     * Set distance to consider a match
     * @param radius
     */
    public static void setRadius(double radius) {
        Matcher.radius = radius;
    }

    /**
     * Add a ride to the matcher
     * @param user providing or requiring a ride
     * @param from origin location
     * @param to destination location
     * @param plate of then car (if null then it is a passenger)
     * @param cost of the ride (how must you charge, if you are the driver)
     * @return ride identifier
     */
    public long addRide(User user, Location from, Location to, String plate, float cost) {
        Ride ride = new Ride(user, from, to, plate, cost);
        rides.put(ride.getId(), ride);

        quadtree.insert(ride);
        return ride.getId();
    }

    /**
     * Update current location of ride with given id. If ride is not yet matched, returns a set {@link RideMatch}.
     * Proposed ride matches are currently near (use {@link PointQuadtree}) have different roles (one is a driver,
     *  the other a passenger) and go almost to the same destination (differ by radius).
     * @param rideId
     * @param current
     * @return
     */
    public SortedSet<RideMatch> updateRide(long rideId, Location current) {
        Ride ride = rides.get(rideId);
        if (ride == null || ride.isMatched())
            return new TreeSet<>();

        // Remove, update the position and reinsert in the quadtree
        quadtree.delete(ride);
        ride.setCurrent(current);
        quadtree.insert(ride);

        Set<Ride> nearby = quadtree.findNear(current.x(), current.y(), radius);
        SortedSet<RideMatch> matches = new TreeSet<>(ride.getComparator());

        for (Ride other : nearby) {
            RideMatch match = new RideMatch(ride, other);

            if (match.matchable()) {
                rideMatches.put(match.getId(), match);
                matches.add(match);
            }
        }

        return matches;
    }

    /**
     * Accept the proposed match (identified by {@code matchId}) for given ride (identified by {@code rideId})
     * @param rideId id of ride
     * @param matchId of match to accept
     */
    public void acceptMatch(long rideId, long matchId) {
        Ride ride = rides.get(rideId);
        RideMatch match = rideMatches.get(matchId);
        ride.setMatch(match);
        quadtree.delete(ride);
    }

    /**
     * Mark ride as concluded and classify other using stars
     * @param rideId of the ride to conclude
     * @param stars to assign to other user
     */
    public void concludeRide(long rideId, UserStars stars) {
        Ride ride = rides.get(rideId);
        RideMatch match = ride.getMatch();

        Ride other = match.getOppositeRide(ride);
        User otherUser = other.getUser();

        otherUser.addStars(stars, other.getRideRole());

        rideMatches.remove(match.getId());
    }
}
