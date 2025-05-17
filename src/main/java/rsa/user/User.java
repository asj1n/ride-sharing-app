package rsa.user;

import rsa.match.PreferredMatch;
import rsa.ride.RideRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A user of the Ride Sharing App.
 * An instance of this class records the user's authentication and other relevant data.
 */
public class User implements Serializable {

    private final String key;
    private final String nick;
    private String name;
    private final HashMap<String, Car> cars;
    private final List<UserStars> driverStars;
    private final List<UserStars> passengerStars;
    private PreferredMatch preferredMatch = PreferredMatch.BETTER;

    /**
     * Creates a User instance. This is the only constructor and is package private.
     * Hence, users can only be instanced in this package, using the method Users.register(String, String).
     * @param nick of user, must be unique
     * @param name of user
     */
    User(String nick, String name) {
        this.nick = nick;
        this.name = name;
        this.key = generateKey();
        this.cars = new HashMap<>();
        this.driverStars = new ArrayList<>();
        this.passengerStars = new ArrayList<>();
    }

    /**
     * The nick of this user: Cannot be changed as it a key.
     * @return nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * Name of user
     * @return user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get user authentication key
     * @return key of user
     */
    public String getKey() {
        return key;
    }

    /**
     * Car with given license plate
     * @param plate of car
     * @return car
     */
    public Car getCar(String plate) {
        return cars.get(plate);
    }

    /**
     * Current preference for sorting matches. Defaults to BETTER
     * @return preferred match by this user
     */
    public PreferredMatch getPreferredMatch() {
        return preferredMatch;
    }

    /**
     * Change user's name
     * @param name to change
     */
    public void setName(String name) {
        this.name = name;
        // generateKeu();
    }

    /**
     * Change preference for sorting matches
     * @param preferredMatch to set for this user
     */
    public void setPreferredMatch(PreferredMatch preferredMatch) {
        if (preferredMatch == null) {
            this.preferredMatch = PreferredMatch.BETTER;
        } else {
            this.preferredMatch = preferredMatch;
        }
    }

    /**
     * A key is generated to enable user authentication.
     * @return key for this user
     * @implNote The ID is an UUID generated with the static method
     * {@link UUID#nameUUIDFromBytes(byte[])} from the nick and name.
     */
    String generateKey() {
        String nickAndName = nick + name;
        return UUID.nameUUIDFromBytes(nickAndName.getBytes()).toString();
    }

    /**
     * Authenticates given key against the stored private key.
     * @param key to check
     * @return true is keys match; otherwise false
     */
    boolean authenticate(String key) {
        return this.key.equals(key);
    }

    /**
     * Bind a car to this user. Can be used to change car features.
     * @param car to add
     */
    public void addCar(Car car) {
        cars.put(car.getPlate(), car);
    }

    /**
     * Remove binding between use and car
     * @param plate of car to remove from this user
     */
    void deleteCar(String plate) {
        cars.remove(plate);
    }

    /**
     * Add stars to user according to a role. The registered values are used to compute an average.
     * @param moreStars to add to this user
     * @param role in which stars are added
     */
    public void addStars(UserStars moreStars, RideRole role) {
        switch (role) {
            case DRIVER -> driverStars.add(moreStars);
            case PASSENGER -> passengerStars.add(moreStars);
        }
    }

    /**
     * Returns the average number of stars in given role
     * @param role of user
     * @return average number of stars
     */
    public float getAverage(RideRole role) {
        List<UserStars> roleReviews = (role == RideRole.DRIVER) ? driverStars : passengerStars;

        if (roleReviews.isEmpty()) {
            return 0;
        }

        int totalRoleStars = 0;

        for (UserStars review : roleReviews) {
            totalRoleStars += review.getStars();
        }

        return (float) totalRoleStars / roleReviews.size();
    }
}
