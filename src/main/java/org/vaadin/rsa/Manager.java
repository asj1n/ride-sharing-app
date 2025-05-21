package org.vaadin.rsa;

import org.vaadin.rsa.match.Location;
import org.vaadin.rsa.match.Matcher;
import org.vaadin.rsa.match.PreferredMatch;
import org.vaadin.rsa.match.RideMatch;
import org.vaadin.rsa.ride.RideRole;
import org.vaadin.rsa.user.User;
import org.vaadin.rsa.user.UserStars;
import org.vaadin.rsa.user.Users;

import java.util.List;
import java.util.Set;

/**
 * An instance of this class is responsible for managing the ride-sharing service,
 * handling user requests and matching their rides. The methods of this class are
 * those needed by web client thus it follows the Facade design pattern.
 * It also follows the Singleton design pattern to provide a single instance of this class to the application
 */
public class Manager {
    private static Manager instance;
    private static Matcher matcher;
    private static Users users;

    /**
     * Private constructor to enforce Singleton pattern
     */
    private Manager() {
    }

    /**
     * Returns the single instance of this class as proposed in the singleton design pattern.
     * RideSharingAppException
     * @return instance of this class
     * @throws RideSharingAppException if I/O error occurs reading users serialization
     */
    public static Manager getInstance() throws RideSharingAppException {
        if (instance == null) {
            instance = new Manager();
            matcher = new Matcher();
            users = Users.getInstance();
        }

        return instance;
    }

    /**
     * Resets singleton for unit testing purposes.
     */
    void reset() {
        instance = null;
        matcher = new Matcher();
        users.reset();
    }

    /**
     * Register a player with given nick and name. Changes are stored in serialization file
     * @param nick of user
     * @param name of user
     * @return <code>true</code> if registered and <code>false</code> otherwise
     * @throws RideSharingAppException if I/O error occurs when serializing data
     */
    public User register(String nick, String name) throws RideSharingAppException {
        return users.register(nick, name);
    }

    /**
     * Current preferred match for given authenticated user
     * @param nick of user
     * @param key of user
     * @return the current preferred match for this user
     * @throws RideSharingAppException if authentication fails
     */
    public PreferredMatch getPreferredMatch(String nick, String key)
            throws RideSharingAppException {

        if (users.authenticate(nick, key)) {
            User user = users.getUser(nick);
            return user.getPreferredMatch();
        }

        throw new RideSharingAppException("Error while getting user PreferredMatch. " +
                                            "Failed authentication for user " + nick);
    }

    /**
     * Set preferred match for given authenticated user
     * @param nick of user
     * @param key of user
     * @param preferred kind of match
     * @throws RideSharingAppException if authentication fails
     */
    public void setPreferredMatch(String nick, String key, PreferredMatch preferred)
            throws RideSharingAppException {

        if (users.authenticate(nick, key)) {
            User user = users.getUser(nick);
            user.setPreferredMatch(preferred);
            return;
        }

        throw new RideSharingAppException("Error while setting user PreferredMatch. " +
                                            "Failed authentication for user " + nick);
    }

    /**
     * Add a ride for user with given nick, from and to the given locations.
     * A car license plate must be given if user is the driver, or null if passenger.
     * @param nick of user
     * @param key of user
     * @param from origin's location
     * @param to destination's location
     * @param plate of car (null if passenger)
     * @param cost of the ride (how must you charge, if you are the driver)
     * @return id of created ride
     * @throws RideSharingAppException if authentication fails
     */
    public long addRide(String nick, String key,
                        Location from, Location to,
                        String plate, float cost) throws RideSharingAppException {

        if (users.authenticate(nick, key)) {
            User user = users.getUser(nick);
            return matcher.addRide(user, from, to, plate, cost);
        }

        throw new RideSharingAppException("Error while adding ride. " +
                                            "Failed authentication for user " + nick);
    }

    /**
     * Update current location of user and receive a set of proposed ride matches
     * @param rideId of ride to update
     * @param current location of user
     * @return A {@link Set} of {@link RideMatch}
     */
    public Set<RideMatch> updateRide(long rideId, Location current) {
        return matcher.updateRide(rideId, current);
    }

    /**
     * Accept a match.
     * @param rideId id of the ride to match
     * @param matchId id of the match to consider
     */
    public void acceptMatch(long rideId, long matchId) {
        matcher.acceptMatch(rideId, matchId);
    }

    /**
     * Conclude a ride and provide feedback on the other partner
     * @param rideId of the ride to conclude
     * @param classification of the ride partner (in stars)
     */
    public void concludeRide(long rideId, UserStars classification) {
        matcher.concludeRide(rideId, classification);
    }

    /**
     * The average number of stars of given user in given role
     * @param nick of user
     * @param role of interest
     * @return average stars on user in role
     * @throws RideSharingAppException if user's nick is not found
     */
    public double getAverage(String nick, RideRole role) throws RideSharingAppException {
        if (users.getUser(nick) != null) {
            User user = users.getUser(nick);
            return user.getAverage(role);
        }

        throw new RideSharingAppException("Error while getting average stars. " +
                                            "User " + nick + " not found");
    }

    /**
     * Returns Users instance associated with the Manager class
     * @return Users
     */
    public List<User> getUsers() {
        return users.getUsers();
    }

    /**
     * Deletes a car registered to a given user
     * @param nick of user
     * @param key of user
     * @param plate of car
     * @throws RideSharingAppException if failure when authenticating user
     */
    public void deleteCar(String nick, String key, String plate) throws RideSharingAppException {
        if (users.authenticate(nick, key)) {
            users.deleteUserCar(nick, plate);
            return;
        }

        throw new RideSharingAppException("Error while deleting car. " +
                                            "Failed authentication for user " + nick);
    }
}
