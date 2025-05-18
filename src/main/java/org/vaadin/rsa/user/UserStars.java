package org.vaadin.rsa.user;

/**
 * Classification to a ride provided by the other user.
 * <b>Note:</b> values() and valueOf() are common to all enumerations and don't need to be implemented.
 */
public enum UserStars {
    ONE_STAR,     // Lousy ride
    TWO_STARS,    // Bad ride
    THREE_STARS,  // Average ride
    FOUR_STARS,   // Good ride
    FIVE_STARS;   // Great ride


    /**
     * Get number of stars as an integer
     * @return number of stars
     */
    public int getStars() {
        return switch (this) {
            case ONE_STAR -> 1;
            case TWO_STARS -> 2;
            case THREE_STARS -> 3;
            case FOUR_STARS -> 4;
            case FIVE_STARS -> 5;
        };
    }
}
