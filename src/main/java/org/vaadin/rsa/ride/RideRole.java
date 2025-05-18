package org.vaadin.rsa.ride;

/**
 * The role of the user in a ride.
 *
 * <p><b>Note:</b> values() and valueOf() are common to all enumerations and don't need to be implemented.
 */
public enum RideRole {
    DRIVER,     // This user is driving the car
    PASSENGER;  // This user is the passenger

    /**
     * The other role: if driver then passenger, otherwise driver
     * @return other role
     */
    public RideRole other() {
        return switch (this) {
            case DRIVER -> PASSENGER;
            case PASSENGER -> DRIVER;
        };
    }
}
