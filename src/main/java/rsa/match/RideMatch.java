package rsa.match;

import rsa.ride.Ride;
import rsa.ride.RideRole;
import rsa.user.Car;

/**
 * A match between 2 rides. Each has specific role, either as driver or as passenger and they must be different.
 * It is assumed that both rides have the same destination, although not checked in this class.
 */
public class RideMatch {

    private static long rideMatchCounter = 1;

    private final long id;
    private final Ride left;
    private final Ride right;

    /**
     * Create a possible ride match for a pair of rides (rides have no particular order)
     * @param left ride
     * @param right ride
     */
    public RideMatch(Ride left, Ride right) {
        this.left = left;
        this.right = right;
        this.id = rideMatchCounter++;
    }

    /**
     * Get car used in this ride
     * @return car used in ride
     */
    public Car getCar() {
        if (left.isDriver()) {
            String plate = left.getPlate();
            return left.getUser().getCar(plate);
        }
        if (right.isDriver()) {
            String plate = right.getPlate();
            return right.getUser().getCar(plate);
        }

        return null;
    }

    /**
     * Cost of this ride, paid by the passenger to the driver
     * @return cost of this ride
     */
    public float getCost() {
        if (left.isDriver())
            return left.getCost();

        return right.getCost();
    }

    /**
     * Generated unique identifier of this ride match.
     * @return this ride match identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Get name of user with given role
     * @param role of user in match
     * @return name of user with given role
     */
    public String getName(RideRole role) {
        if (left.getRideRole() == role) {
            return left.getUser().getName();
        }

        return right.getUser().getName();
    }

    /**
     * Ride of user with given role
     * @param role of user
     * @return the driver Ride
     */
    public Ride getRide(RideRole role) {
        if (left.getRideRole() == role) {
            return left;
        }
        return right;
    }

    /**
     * Get average number of stars of user with given role
     * @param role of user in match
     * @return stars average of user with given role
     */
    public float getStars(RideRole role) {
        if (left.getRideRole() == role) {
            return left.getUser().getAverage(role);
        }
        return right.getUser().getAverage(role);
    }

    /**
     * The location of a user with given role
     * @param role of user in match
     * @return location of user with given role
     */
    public Location getWhere(RideRole role) {
        if (left.getRideRole() == role) {
            return left.getCurrent();
        }
        return right.getCurrent();
    }

    /**
     * Are these rides matchable?
     * <ul>
     *   <li>Do they fill both roles (user and passenger)?</li>
     *   <li>Are they both unmatched?</li>
     *   <li>Are they currently in (roughly) the same place?</li>
     *   <li>Are they both going to (roughly) the same destination?</li>
     * </ul>
     * Locations are considered different if their distance exceeds radius {@link Matcher#getRadius()}.
     * @return true if it's a match, false otherwise.
     */
    boolean matchable() {
        if (left == null || right == null) {
            return false;
        }

        if (left.getRideRole() == right.getRideRole()) {
            return false;
        }

        if (left.isMatched() || right.isMatched()) {
            return false;
        }

        if (!isCloseEnough(left.getCurrent(), right.getCurrent())) {
            return false;
        }

        return isCloseEnough(left.getTo(), right.getTo());
    }

    /**
     * Get the opposite role of a given ride in a {@link RideMatch}.
     * @param ride to get the opposite of
     * @return opposite ride
     */
    public Ride getOppositeRide(Ride ride) {
        if (left.getRideRole() == ride.getRideRole()) {
            return right;
        }
        if (right.getRideRole() == ride.getRideRole()) {
            return left;
        }

        return null;
    }

    /**
     * Finds if the distance between to locations is under a {@link Matcher#getRadius()}.
     * @param left location
     * @param right location
     * @return true if distance is less than or equal to the radius, false otherwise
     */
    private boolean isCloseEnough(Location left, Location right) {
        return Math.sqrt(Math.pow(left.x() - right.x(), 2) + Math.pow(left.y() - right.y(), 2)) <= Matcher.getRadius();
    }
}
