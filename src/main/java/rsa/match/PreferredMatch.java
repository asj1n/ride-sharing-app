package rsa.match;

/**
 * Preferred way to sort matches. Users will set their preferences using this values.
 * <p><b>Note: </b> values() and valueOf() are common to all enumerations and don't need to be implemented.
 */
public enum PreferredMatch {
    BETTER,  // Prefer to ride with better users (higher average stars; this is the default).
    CHEAPER, // Prefer cheaper rides (if you are a passenger).
    CLOSER;  // Prefer to ride with nearby users.
}
