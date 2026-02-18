package rsa;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rsa.match.Location;
import rsa.match.Matcher;
import rsa.match.RideMatch;
import rsa.ride.RideRole;
import rsa.user.Car;
import rsa.user.User;
import rsa.user.UserStars;
import rsa.user.Users;

import java.io.File;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;
import static rsa.match.PreferredMatch.*;

/**
* Template for a test class on Manager - YOU NEED TO IMPLEMENT THESE TESTS!
* 
*/
public class ManagerTest extends rsa.TestData {
	public static final File USERS_FILE = new File("test_users.ser");

	static Manager manager;
	static Matcher matcher;
	static Users users;

	Location from = new Location(X1,Y1);
	Location to   = new Location(X2,Y2);
	Location other = new Location(X3,Y3);
	
	@BeforeAll
	public static void setUpClass() throws RideSharingAppException {
		Users.setUsersFile(USERS_FILE);

		manager = Manager.getInstance();
		users = Users.getInstance();
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		manager.reset();
		Users.setUsersFile(USERS_FILE);
	}

	@AfterAll
	public static void tearDownClass() {
		if(USERS_FILE.exists())
			USERS_FILE.delete();
	}

	/**
	 * Check user registration with invalid nicks, duplicate nicks, multiple users
	 * 
	 * @throws RideSharingAppException on reading serialization file (not tested)
	 */
	@Test
	public void testRegister() throws RideSharingAppException {
		assertAll(
			() -> {
				assertNull(manager.register("0U", "User A"), "Invalid nick");
				assertNull(manager.register("User 0", "User B"), "Invalid nick");
				assertNull(manager.register("u?*", "User C"), "Invalid nick");
			},
			() -> {
				assertNotNull(manager.register("testUser123", "Test User"), "Valid registration");
				assertNull(manager.register("testUser123", "Test User"), "Duplicate registration");
			},
			() -> {
				assertNotNull(manager.register(NICKS[0], NAMES[0]), "Valid registration");
				assertNotNull(manager.register(NICKS[1], NAMES[1]), "Valid registration");
				assertNotNull(manager.register(NICKS[2], NAMES[2]), "Valid registration");
			}
		);
	}
	
	/**
	 * Check password update, with valid password, old password and wrong password
	 *    
	 * @throws RideSharingAppException on reading serialization file (not tested)
	 */
	@Test
	public void testUpdatePassword() throws RideSharingAppException {
		// fail();
	}

	/**
	 * Check authentication valid and invalid tokens and multiple users
	 * 
	 * @throws RideSharingAppException on reading serialization file (not tested)
	 */
	@Test
	public void testAuthenticate() throws RideSharingAppException {
		User userZero = manager.register(NICKS[0], NAMES[0]);
		User userOne = manager.register(NICKS[1], NAMES[1]);
		User userTwo = manager.register(NICKS[2], NAMES[2]);

		assertNotNull(userZero);
		assertNotNull(userOne);
		assertNotNull(userTwo);

		assertTrue(users.authenticate(userZero.getNick(), userZero.getKey()), "Authentication successful");
		assertTrue(users.authenticate(userOne.getNick(), userOne.getKey()), "Authentication successful");
		assertTrue(users.authenticate(userTwo.getNick(), userTwo.getKey()), "Authentication successful");

		assertFalse(users.authenticate(userZero.getNick(), "wrong password"), "Authentication failed");
		assertFalse(users.authenticate(userOne.getNick(), "wrong password"), "Authentication failed");
		assertFalse(users.authenticate(userTwo.getNick(), "wrong password"), "Authentication failed");
	}

	
	@Test
	public void testPreferredMatch() throws RideSharingAppException {
		assertAll(
			() -> {
				assertThrows(RideSharingAppException.class, () -> {
					manager.getPreferredMatch(NICKS[0], NAMES[0]);
				}, "User not found");
			},
			() -> {
				User userZero = manager.register(NICKS[0], NAMES[0]);
				assertEquals(BETTER,
							 manager.getPreferredMatch(userZero.getNick(), userZero.getKey()),
							 "Default Preferred match");
			},
			() -> {
				User userOne = manager.register(NICKS[1], NAMES[1]);
				manager.setPreferredMatch(userOne.getNick(), userOne.getKey(), CHEAPER);
				assertEquals(CHEAPER,
							 manager.getPreferredMatch(userOne.getNick(), userOne.getKey()),
							 "CHEAPER Preferred match");
			},
			() -> {
				User userTwo = manager.register(NICKS[2], NAMES[2]);
				assertThrows(RideSharingAppException.class, () -> {
					manager.setPreferredMatch(userTwo.getNick(), "random password", CHEAPER);
				});
			}
		);
	}


	/**
	 * Check if rides don't match when both are drivers
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testRidesDontMatchBothDrivers() throws RideSharingAppException {
		User driver = manager.register(NICKS[0], NAMES[0]);
		User secondDriver = manager.register(NICKS[1], NAMES[1]);

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long secondDriverRideId = manager.addRide(secondDriver.getNick(), secondDriver.getKey(), from, to, PLATES[1], COSTS[0]);

		Set<RideMatch> driverMatches    = manager.updateRide(driverRideId, from);
		Set<RideMatch> secondDriverMatches = manager.updateRide(secondDriverRideId, from);

		assertEquals(0, driverMatches.size());
		assertEquals(0, secondDriverMatches.size());
	}
	
	/**
	 * Check if rides don't match when both are passengers
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testRidesDontMatchBothPassengers() throws RideSharingAppException {
		User passenger = manager.register(NICKS[0], NAMES[0]);
		User secondPassenger = manager.register(NICKS[1], NAMES[1]);

		long passengerRideId    = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, PLATES[0], COSTS[0]);
		long secondPassengerRideId = manager.addRide(secondPassenger.getNick(), secondPassenger.getKey(), from, to, PLATES[1], COSTS[0]);

		Set<RideMatch> passengerMatches = manager.updateRide(passengerRideId, from);
		Set<RideMatch> secondPassengerMatches = manager.updateRide(secondPassengerRideId, from);

		assertEquals(0, passengerMatches.size());
		assertEquals(0, secondPassengerMatches.size());
	}

	
	/**
	 * Check if rides don't match when destination is different  
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testRidesDontMatchDifferentDestination() throws RideSharingAppException {
		User driver = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);

		long driverRideId = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, other, null, COSTS[0]);

		Set<RideMatch> driverMatches = manager.updateRide(driverRideId, from);
		Set<RideMatch> passengerMatches = manager.updateRide(passengerRideId, from);

		assertEquals(0, driverMatches.size());
		assertEquals(0, passengerMatches.size());
	}

	/**
	 * Check if rides don't match when current position is different  
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testRidesDontMatchWhenInDifferentPositions() throws RideSharingAppException {
		User driver = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);

		long driverRideId = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), other, to, null, COSTS[0]);

		Set<RideMatch> driverMatches = manager.updateRide(driverRideId, from);
		Set<RideMatch> passengerMatches = manager.updateRide(passengerRideId, other);

		assertEquals(0, driverMatches.size());
		assertEquals(0, passengerMatches.size());
	}


	/**
	 * Simple match: both rides with same path (origin and destination)
	 * One is driver and other passenger.
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testSimpleMatch() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);

		long driverRideId = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);

		SortedSet<RideMatch> driverMatches = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(1, passengerMatches.size());

		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[0], driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(NAMES[1], driverMatch.getName(RideRole.PASSENGER));
		assertEquals(NAMES[1], passengerMatch.getName(RideRole.PASSENGER));

		manager.acceptMatch(driverRideId, passengerMatch.getId());
		manager.acceptMatch(passengerRideId, driverMatch.getId());

		assertEquals(0, driver.getAverage(RideRole.DRIVER),DELTA);
		assertEquals(0, passenger.getAverage(RideRole.PASSENGER),DELTA);

		manager.concludeRide(driverRideId, UserStars.FOUR_STARS);
		manager.concludeRide(passengerRideId, UserStars.FIVE_STARS);

		assertEquals(5, driver.getAverage(RideRole.DRIVER),DELTA);
		assertEquals(4, passenger.getAverage(RideRole.PASSENGER),DELTA);
	}

	/**
	 * Double match: two drivers with same path (origin and destination)
	 * First has more starts and is used the default preference (BETTER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchDefault1() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[0]);

		driver.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[0], driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[0], driverMatch.getCar().getPlate());
		assertEquals(PLATES[0], passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination)
	 * Second has more stars and is used the default preference (BETTER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchDefault2() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[0]);

		driver.addStars(UserStars.ONE_STAR, RideRole.DRIVER);
		other.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch otherMatch = otherMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[2], otherMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[2], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[2], otherMatch.getCar().getPlate());
		assertEquals(PLATES[2], passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination)
	 * First has more starts and is used the better driver preference (BETTER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchBetter1() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[0]);

		manager.setPreferredMatch(passenger.getNick(), passenger.getKey(), BETTER);

		driver.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[0], driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[0], driverMatch.getCar().getPlate());
		assertEquals(PLATES[0], passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination)
	 * Second has more stars and is used the better driver preference (BETTER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchBetter2() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[0]);

		manager.setPreferredMatch(passenger.getNick(), passenger.getKey(), BETTER);

		driver.addStars(UserStars.ONE_STAR, RideRole.DRIVER);
		other.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch otherMatch = otherMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[2], otherMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[2], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[2], otherMatch.getCar().getPlate());
		assertEquals(PLATES[2], passengerMatch.getCar().getPlate());
	}
	
	
	/**
	 * Double match: two drivers with same path (origin and destination)
	 * First has more starts and is used the cheapest ride preference (CHEAPER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchCheaper1() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[2]);

		manager.setPreferredMatch(passenger.getNick(), passenger.getKey(), CHEAPER);

		driver.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[0], driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[0], driverMatch.getCar().getPlate());
		assertEquals(PLATES[0], passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination)
	 * Second has more stars and is used the cheapest ride preference (CHEAPER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchCheaper2() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[2]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[0]);

		manager.setPreferredMatch(passenger.getNick(), passenger.getKey(), CHEAPER);

		driver.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch otherMatch = otherMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[2], otherMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[2], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[2], otherMatch.getCar().getPlate());
		assertEquals(PLATES[2], passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination)
	 * First has more starts and is used the closer ride preference (CLOSER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchCloser1() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		Location near = new Location(X1+RADIUS,Y1);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[0]);

		manager.setPreferredMatch(passenger.getNick(), passenger.getKey(), CLOSER);

		driver.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, near);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[0], driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[0], driverMatch.getCar().getPlate());
		assertEquals(PLATES[0], passengerMatch.getCar().getPlate());
	}
	
	
	/**
	 * Double match: two drivers with same path (origin and destination)
	 * Second has more stars and is used the closer ride preference (CLOSER)
	 * @throws RideSharingAppException 
	 */
	@Test
	public void testDoubleDriverMatchCloser2() throws RideSharingAppException {
		User driver    = manager.register(NICKS[0], NAMES[0]);
		User passenger = manager.register(NICKS[1], NAMES[1]);
		User other     = manager.register(NICKS[2], NAMES[2]);

		Location near = new Location(X1+RADIUS,Y1);

		driver.addCar(new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]));
		other.addCar(new Car(PLATES[2], MAKES[2], MODELS[2], COLORS[2]));

		long driverRideId    = manager.addRide(driver.getNick(), driver.getKey(), from, to, PLATES[0], COSTS[0]);
		long passengerRideId = manager.addRide(passenger.getNick(), passenger.getKey(), from, to, null, COSTS[0]);
		long otherRideId     = manager.addRide(other.getNick(), other.getKey(), from, to, PLATES[2], COSTS[0]);

		manager.setPreferredMatch(passenger.getNick(), passenger.getKey(), CLOSER);

		driver.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);

		SortedSet<RideMatch> driverMatches    = (SortedSet<RideMatch>) manager.updateRide(driverRideId, near);
		SortedSet<RideMatch> otherMatches     = (SortedSet<RideMatch>) manager.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = (SortedSet<RideMatch>) manager.updateRide(passengerRideId, from);

		assertEquals(1, driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1, otherMatches.size());

		RideMatch otherMatch = otherMatches.first();
		RideMatch passengerMatch = passengerMatches.first();

		assertEquals(NAMES[2], otherMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[2], passengerMatch.getName(RideRole.DRIVER));

		assertEquals(PLATES[2], otherMatch.getCar().getPlate());
		assertEquals(PLATES[2], passengerMatch.getCar().getPlate());
	}

	/**
	 * Check if star average is well computed when stars are added
	 * for user in the two roles (driver and passenger)
	 */
	@Test
	public void testStars() throws RideSharingAppException {
		assertThrows(RideSharingAppException.class, () -> {
			manager.getAverage(NICKS[0], RideRole.DRIVER);
		}, "User not registered");

		User user = manager.register(NICKS[0], NAMES[0]);

		assertEquals(0, manager.getAverage(user.getNick(), RideRole.DRIVER), DELTA);
		assertEquals(0, manager.getAverage(user.getNick(), RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);

		assertEquals(4, manager.getAverage(user.getNick(), RideRole.DRIVER), DELTA);
		assertEquals(0, manager.getAverage(user.getNick(), RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);

		assertEquals(4, manager.getAverage(user.getNick(), RideRole.DRIVER), DELTA);
		assertEquals(0, manager.getAverage(user.getNick(), RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);

		assertEquals((4D + 4D + 5D) / 3D, manager.getAverage(user.getNick(), RideRole.DRIVER), DELTA);
		assertEquals(0, manager.getAverage(user.getNick(), RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.THREE_STARS, RideRole.DRIVER);
		user.addStars(UserStars.FIVE_STARS, RideRole.PASSENGER);

		assertEquals((4D + 4D + 5D + 3D) / 4D, manager.getAverage(user.getNick(), RideRole.DRIVER), DELTA);
		assertEquals(5, manager.getAverage(user.getNick(), RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FOUR_STARS, RideRole.PASSENGER);

		assertEquals(4.5D, manager.getAverage(user.getNick(), RideRole.PASSENGER), DELTA);
	}
}
