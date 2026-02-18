package org.vaadin.rsa.ride;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vaadin.rsa.RideSharingAppException;
import org.vaadin.rsa.TestData;
import org.vaadin.rsa.match.PreferredMatch;
import org.vaadin.rsa.match.RideMatch;
import org.vaadin.rsa.match.Location;
import org.vaadin.rsa.user.User;
import org.vaadin.rsa.user.UserStars;
import org.vaadin.rsa.user.Users;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link org.vaadin.rsa.ride.Ride}.
 * Depends on:
 * <ul>
 *      <li> {@link org.vaadin.rsa.match.RideMatch};
 *      <li> {@link Location};
 *      <li> {@link org.vaadin.rsa.user.User};</li>
 *      <li> {@link org.vaadin.rsa.user.Users}.</li>
 * </ul>
 */
class RideTest extends TestData {
    static Users users;

    Ride driverRide, passengerRide;
    User user;
    Location from, to;
    RideMatch match;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        users =Users.getInstance();
    }

    @BeforeEach
    void setUp() throws RideSharingAppException {
        user = users.getOrCreateUser(NICK,NAME);
        from = new Location(X1,Y1);
        to = new Location(X2,Y2);

        driverRide = new Ride(user,from,to,PLATE,COST);
        passengerRide = new Ride(user,from,to,null,COST);

        match = new RideMatch(driverRide,passengerRide);
    }

    /**
     * Check if rides have0 non-negative IDs and are different.
     */
    @Test
    void getId() {
        assertTrue(driverRide.getId() >= 0);
        assertTrue(passengerRide.getId() >= 0);

        assertNotEquals(driverRide.getId(), passengerRide.getId());
    }

    /**
     * Check if repeated rides have different IDs,
     * event if created with the same arguments.
     */
    @RepeatedTest(REPETITIONS)
    void getAnotherId() {
        assertNotEquals(driverRide.getId(), new Ride(user,from,to,PLATE,COST).getId());
    }

    /**
     * Check users defined on creation.
     */
    @Test
    void getUser() {
        assertAll(
                () -> assertEquals(user, driverRide.getUser()),
                () -> assertEquals(user, passengerRide.getUser())
        );

    }

    /**
     * Check if users can be changed
     * @param nick of user
     * @param name of user
     * @throws RideSharingAppException on backup I/O error
     */
    @ParameterizedTest
    @MethodSource("nickAndNameProvider")
    void setUser(String nick, String name) throws RideSharingAppException {
        User user = users.getOrCreateUser(nick,name);

        driverRide.setUser(user);
        passengerRide.setUser(user);

        assertAll(
                () -> assertEquals( user, driverRide.getUser()),
                () -> assertEquals( user, passengerRide.getUser())
        );
    }

    /**
     * Check plates set on construction
     */
    @Test
    void getPlate() {
        assertAll(
                () -> assertEquals(PLATE, driverRide.getPlate()),
                () -> assertEquals(null, passengerRide.getPlate())
        );
    }

    /**
     * Check if plates can be changed
     */
    @ParameterizedTest
    @MethodSource("plateProvider")
    void setPlate(String plate) {
        driverRide.setPlate(plate);

        assertEquals(plate, driverRide.getPlate());
    }

    /**
     * Check if is driver for both driver and passenger rides
     */
    @Test
    void isDriver() {
        assertAll(
                () -> assertTrue(driverRide.isDriver()),
                () -> assertFalse(passengerRide.isDriver())
        );
    }

    /**
     * Check roles of rides
     */
    @Test
    void getRideRole() {
        assertAll(
                () -> assertEquals(RideRole.DRIVER, driverRide.getRideRole()),
                () -> assertEquals(RideRole.PASSENGER,passengerRide.getRideRole())
        );
    }

    /**
     * Check if is passenger
     */
    @Test
    void isPassenger() {
        assertAll(
                () -> assertFalse(driverRide.isPassenger()),
                () -> assertTrue(passengerRide.isPassenger())
        );
    }

    /**
     * Check costs set on construction
     */
    @Test
    void getCost() {
        assertAll(
                () -> assertEquals(COST, driverRide.getCost()),
                () -> assertEquals(COST, passengerRide.getCost())
        );
    }

    /**
     * Check if cost can be changed
     * @param cost of ride
     */
    @ParameterizedTest
    @MethodSource("costProvider")
    void setCost(float cost) {
        driverRide.setCost(cost);
        passengerRide.setCost(cost);

        assertAll(
                () -> assertEquals(cost, driverRide.getCost()),
                () -> assertEquals(cost, passengerRide.getCost())
        );
    }

    /**
     * Check the from field set on construction
     */
    @Test
    void getFrom() {
        assertAll(
                () -> assertEquals(from, driverRide.getFrom()),
                () -> assertEquals(from, passengerRide.getFrom())
        );

    }

    /**
     * Stream of locations for testing.
     * Not in test data to avoid generalizing the dependency to {@code Location},
     * @return stream of {@code Location}
     */
    private static Stream<Location> locationProvider() {
        return Stream.of( new Location(X1,Y1), new Location(X2,Y2), new Location(X3,Y3));
    }

    /**
     *
     */
    @ParameterizedTest
    @MethodSource("locationProvider")
    void setFrom(Location location) {
        driverRide.setFrom(location);
        passengerRide.setFrom(location);

        assertAll(
                () -> assertEquals(location, driverRide.getFrom()),
                () -> assertEquals(location, passengerRide.getFrom())
        );
    }

    /**
     * Check the default to location
     */
    @Test
    void getTo() {
        assertAll(
                () -> assertEquals(to, driverRide.getTo()),
                () -> assertEquals(to, passengerRide.getTo())
        );

    }

    /**
     * Check if the to field can be changed
     * @param location to change
     */
    @ParameterizedTest
    @MethodSource("locationProvider")
    void setTo(Location location) {
        driverRide.setTo(location);
        passengerRide.setTo(location);

        assertAll(
                () -> assertEquals(location, driverRide.getTo()),
                () -> assertEquals(location, passengerRide.getTo())
        );
    }

    /**
     * Check the initial current location
     */
    @Test
    void getCurrent() {
        assertAll(
                () -> assertEquals(from,driverRide.getCurrent()),
                () -> assertEquals(from,passengerRide.getCurrent())
        );
    }

    /**
     * Check if the current location can be changed
     * @param location of ride
     */
    @ParameterizedTest
    @MethodSource("locationProvider")
    void setCurrent(Location location) {
        driverRide.setCurrent(location);
        passengerRide.setCurrent(location);

        assertAll(
                () -> assertEquals(location,driverRide.getCurrent()),
                () -> assertEquals(location,passengerRide.getCurrent())
        );
    }

    /**
     * Check the initial match.
     */
    @Test
    void getMatch() {
        assertAll(
                () -> assertNull(driverRide.getMatch()),
                () -> assertEquals(null, passengerRide.getMatch())
        );
    }

    @Test
    void setMatch() {
       passengerRide.setMatch(match);
       driverRide.setMatch(match);
       assertAll(
               () -> assertEquals(match, driverRide.getMatch()),
               () ->    assertEquals(match, passengerRide.getMatch())
       );
    }

    /**
     * Check is matched
     */
    @Test
    void isMatched() {
        assertAll(
                () -> assertFalse(driverRide.isMatched(),"on creation rides are unmatched"),
                () -> {
                    driverRide.setMatch(match);
                    assertTrue(driverRide.isMatched(),"should be matched");
                }
        );

    }

    /**
     * Check the ride's X coordinate.
     */
    @Test
    void x() {
        assertAll(
                () -> assertEquals(X1,driverRide.x(),"default X location expected"),
                () -> assertEquals(X1,passengerRide.x(),"default X location expected")
        );
    }

    /**
     * Check the ride's Y coordinate.
     */
    @Test
    void y() {
        assertAll(
                () -> assertEquals(Y1,driverRide.y(),"default Y location expected"),
                () -> assertEquals(Y1,passengerRide.y(),"default XY location expected")
        );

    }

    /**
     * Tests on {@code getComparator()}.
     * All tests are from the passenger point-of-view
     * and cover the three preferred kinds of matching.
     */
    @Nested
    class TestGetComparator {

        User otherDriver;
        Ride otherDriverRide;
        RideMatch otherMatch;

        @BeforeEach
        void setUp()  throws RideSharingAppException {
            otherDriver = users.getOrCreateUser(NICKS[2], NAMES[2]);
            otherDriverRide = new Ride(otherDriver, from, to, PLATES[2], COSTS[1]);
            otherMatch = new RideMatch(otherDriverRide,passengerRide);
        }

        /**
         * Test comparator when preferred match is BETTER.
         */
        @Test
        void testBETTER() {
            user.setPreferredMatch(PreferredMatch.BETTER);

            var comparator = passengerRide.getComparator();

            assertAll(
                    () -> assertTrue(comparator.compare(match, otherMatch) == 0),
                    () -> {
                        otherDriver.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
                        assertTrue(comparator.compare(match, otherMatch) > 0);
                    },
                    () -> {
                        user.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
                        assertTrue(comparator.compare(match, otherMatch) < 0);
                    }
            );

        }

        /**
         * Test comparator when preferred match is CHEAPER.
         */
        @Test
        void testCHEAPER() {
            user.setPreferredMatch(PreferredMatch.CHEAPER);

            var comparator = passengerRide.getComparator();

             assertAll(
                     () -> {
                         var otherDriverRide = new Ride(otherDriver, from, to, PLATES[1], COSTS[0]);
                         var otherMatch = new RideMatch(otherDriverRide,passengerRide);

                         assertTrue(comparator.compare(match, otherMatch) == 0);
                         },
                    () -> assertTrue(comparator.compare(match, otherMatch) < 0),
                     () -> {
                         var cheapDriverRide = new Ride(otherDriver,from,to,PLATES[1],2.0F);
                         var cheapMatch = new RideMatch(cheapDriverRide,passengerRide);

                         var expensiveDriverRide = new Ride(otherDriver,from,to,PLATES[2],3.0F);
                         var expensiveMatch = new RideMatch(expensiveDriverRide,passengerRide);

                         assertTrue(comparator.compare(expensiveMatch,cheapMatch) > 0);
                     });
        }

        /**
         * Test comparator when preferred match is CLOSER.
         */
        @Test
        void testCLOSER() {
            user.setPreferredMatch(PreferredMatch.CLOSER);
            var comparator = passengerRide.getComparator();

            assertAll(
                    () -> assertTrue(comparator.compare(match, otherMatch) == 0),
                    () -> {
                        passengerRide.setCurrent( new Location(10,10));

                        driverRide.setCurrent( new Location(20,10));
                        otherDriverRide.setCurrent( new Location(30,10));

                        assertTrue(comparator.compare(match, otherMatch) < 0);
                    },
                    () -> {
                        passengerRide.setCurrent( new Location(28,10));

                        driverRide.setCurrent( new Location(20,10));
                        otherDriverRide.setCurrent( new Location(30,10));

                        assertTrue(comparator.compare(match, otherMatch) > 0);
                    }
            );
        }
    }
}