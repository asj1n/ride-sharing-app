package org.vaadin.rsa.ride;

import org.vaadin.rsa.match.Location;
import org.vaadin.rsa.match.PreferredMatch;
import org.vaadin.rsa.match.RideMatch;
import org.vaadin.rsa.quad.HasPoint;
import org.vaadin.rsa.user.User;

import java.util.Comparator;

import static org.vaadin.rsa.quad.Trie.getDistance;

/**
 * A user's (intention to) ride between two locations. The user can be either the driver of the passenger.
 * There will be an attempt to match this ride with another of complementary type.
 * That is driver's rides will be matched with passenger's rides and vice versa.
 *
 * <p>This class provides a comparator of ride matches adjusted to this ride.
 * Ride matches are sent to clients as {@link RideMatch} instances.
 * If more than one is available then they are sorted.
 * The order depends on ride that is being matched.
 *
 * <p>To produce comparators this class is a concrete participant of the Factory Method design pattern.
 * It implements the {@link RideMatchSorter} interface and subclasses {@link Comparator}
 */
public class Ride implements HasPoint, RideMatchSorter {

    private static long rideCounter = 1;

    private long id;
    private User user;
    private Location from;
    private Location current; // ADDED
    private Location to;
    private String plate;
    private RideRole userRole;
    private float cost;
    private RideMatch rideMatch;

    /**
     * Creates a ride from given arguments. Current location is initialized as the starting point (from)
     * @param user providing or requiring a ride
     * @param from origin location
     * @param to destination location
     * @param plate of then car (if null then it is a passenger)
     * @param cost of the ride (how must you charge, if you are the driver)
     */
    public Ride(User user, Location from, Location to, String plate, float cost) {
        this.user = user;
        this.from = from;
        this.to = to;
        this.plate = plate;
        this.cost = cost;
        this.id = rideCounter++;
        this.userRole = this.plate == null ? RideRole.PASSENGER : RideRole.DRIVER;
        this.current = from;
    }

    /**
     * Cost of this ride (only meaningful for driver)
     * @return the cost
     */
    public float getCost() {
        return cost;
    }

    /**
     * Get current location of this ride
     * @return current location
     */
    public Location getCurrent() {
        return current;
    }

    /**
     * Get the origin of this ride
     * @return the location from which this ride comes
     */
    public Location getFrom() {
        return from;
    }

    /**
     * Generated unique identifier of this ride. Identifiers are non-negative integers.
     * @return ride identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Current match of this ride
     * @return Current match of this ride
     */
    public RideMatch getMatch() {
        return rideMatch;
    }

    /**
     * Car's registration plate for this ride
     * @return the plate (null if passenger)
     */
    public String getPlate() {
        return plate;
    }

    /**
     * Role of user in ride, depending on a car's license plate being registered
     * @return {@link RideRole} depending on plate
     */
    public RideRole getRideRole() {
        return userRole;
    }

    /**
     * Get destination of this ride
     * @return the location to which this ride is going
     */
    public Location getTo() {
        return to;
    }

    /**
     * User of this ride
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Is the user the driver in this ride?
     * @return true if user is the driver, false otherwise
     */
    public boolean isDriver() {
        return userRole == RideRole.DRIVER;
    }

    /**
     * This ride was match with another
     * @return true is this ride is matched
     */
    public boolean isMatched() {
        return this.rideMatch != null;
    }

    /**
     * Is the user the passenger in this ride?
     * @return true if user is the passenger; false otherwise
     */
    public boolean isPassenger() {
        return userRole == RideRole.PASSENGER;
    }

    /**
     * Change cost of this ride (only meaningful for driver)
     * @param cost the cost to set
     */
    public void setCost(float cost) {
        this.cost = cost;
    }

    /**
     * Change current location
     * @param current location to set
     */
    public void setCurrent(Location current) {
        this.current = current;
    }

    /**
     * Change the origin of this ride
     * @param from the location from which this ride will come
     */
    public void setFrom(Location from) {
        // Change the origin of this ride
        this.from = from;
    }

    /**
     * Assign a match to this ride
     * @param match the match to set
     */
    public void setMatch(RideMatch match) {
        this.rideMatch = match;
    }

    /**
     * Change car registration plate for this ride
     * @param plate of car to set (null if passenger)
     */
    public void setPlate(String plate) {
        this.plate = plate;
    }

    /**
     * Change destination of this ride
     * @param to the location to which this ride will go
     */
    public void setTo(Location to) {
        this.to = to;
    }

    /**
     * Change user of this ride
     * @param user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Description copied from interface: {@link HasPoint}
     * <p>Point's X coordinate.
     * @return x coordinate
     */
    @Override
    public double x() {
        return current.x();
    }

    /**
     * Description copied from interface: {@link HasPoint}
     * <p>Point's Y coordinate.
     * @return y coordinate
     */
    @Override
    public double y() {
        return current.y();
    }

    /**
     * Get a comparator of {@link RideMatch} instances for the given ride.
     * Instances of RideMatchInfo are compared based on the preferences of the ride's user ({@link PreferredMatch}).
     * <ul>
     *   <li><b>BETTER</b><br>
     *       the ride with the user in the other role with higher average stars is the smaller</li>
     *   <li><b>CLOSER</b><br>
     *       the ride with the location of the other role closer to the current location of this ride is the smaller</li>
     *   <li><b>CHEAPER</b><br>
     *       the ride with the cheapest cost is the smaller</li>
     * </ul>
     * If the two matches have the same average/distance/cost then they are considered equal (returns 0).
     * @return a comparator of {@link RideMatch}
     */
    @Override
    public Comparator<RideMatch> getComparator() {
        PreferredMatch preference = user.getPreferredMatch();

        return (a, b) -> {
            Ride oppositeRideA = a.getOppositeRide(this);
            Ride oppositeRideB = b.getOppositeRide(this);

            switch (preference) {
                case CHEAPER -> {
                    float costA = oppositeRideA.getCost();
                    float costB = oppositeRideB.getCost();
                    return Float.compare(costA, costB);
                }
                case CLOSER -> {
                    double distanceA = getDistance(current.x(), current.y(), oppositeRideA.getCurrent().x(), oppositeRideA.getCurrent().y());
                    double distanceB = getDistance(current.x(), current.y(), oppositeRideB.getCurrent().x(), oppositeRideB.getCurrent().y());
                    return Double.compare(distanceA, distanceB);
                }
                case BETTER -> {
                    double starsA = oppositeRideA.getUser().getAverage(oppositeRideA.getRideRole());
                    double starsB = oppositeRideB.getUser().getAverage(oppositeRideB.getRideRole());
                    // in this case it is inverted because unlike the others, we want the highest value
                    return Double.compare(starsB, starsA);
                }

                default -> {
                    return 0;
                }
            }
        };
    }
}
