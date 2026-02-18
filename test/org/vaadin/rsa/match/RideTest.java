package org.vaadin.rsa.match;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vaadin.rsa.RideSharingAppException;
import org.vaadin.rsa.TestData;
import org.vaadin.rsa.ride.Ride;
import org.vaadin.rsa.ride.RideRole;
import org.vaadin.rsa.user.User;
import org.vaadin.rsa.user.Users;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.vaadin.rsa.user.UsersTest.USERS_FILE;

public class RideTest extends TestData {
	static Users allUsers;

	Matcher matcher;
	Ride ride;
	User user;
	Location from;
	Location to;

	@BeforeAll
	public static void setUpClass() throws RideSharingAppException {
		Users.setUsersFile(USERS_FILE);
		allUsers = Users.getInstance();
	}

	@AfterAll
	public static void tearDownClass() {
		if(USERS_FILE.exists())
			USERS_FILE.delete();
	}

	@BeforeEach
	public void setUp() throws Exception {
		allUsers.reset();

		matcher = new Matcher();
		from    = new Location(X1,Y1);
		to      = new Location(X2,Y2);
		ride    = new Ride(user,from,to,PLATE,COST);
		user    = allUsers.register(NICK,NAME);
	}

	@Test
	public void testRide() {
		assertNotNull(ride);
	}

	/**
	 * Check that rides have different IDs 
	 */
	@Test
	public void testGetId() {
		Set<Long> rides = new HashSet<>();
		
		for(int i=0; i<MANY_OBJECTS; i++)
			rides.add(new Ride(user,from,to,PLATE,COST).getId());
		
		assertEquals(MANY_OBJECTS,rides.size());
	}

	/**
	 * Check user's setter and getter
	 */
	@ParameterizedTest
	@MethodSource("nickAndNameProvider")
	public void testUser(String nick, String name) throws RideSharingAppException {
		User someUser = allUsers.getOrCreateUser(nick,name);
		Ride someRide = new Ride(someUser,from,to,PLATE,COST);
				
		assertEquals(nick,someRide.getUser().getNick());
	}

	/**
	 * Check default value of getPlate()
	 */
	@Test
	public void testDefaultGetPlate() {
		assertEquals(PLATE, ride.getPlate());
	}

	/**
	 * Check plate's setter and getter
	 */
	@ParameterizedTest
	@MethodSource("plateProvider")
	public void testSetPlate(String plate) {
		ride.setPlate(plate);
		assertEquals(plate,ride.getPlate());
	}

	/**
	 * Check is driver
	 */
	@Test
	public void testIsDriver() {
		assertTrue(ride.isDriver());
		
		ride = new Ride(user,from,to,null,COSTS[0]);
		assertFalse(ride.isDriver());
		
	}

	/**
	 * Check ride role getter
	 */
	@Test
	public void testGetRideRole() {
		assertEquals(RideRole.DRIVER,ride.getRideRole());
		
		ride = new Ride(user,from,to,null,COSTS[0]);
		assertEquals(RideRole.PASSENGER,ride.getRideRole());
	}

	/**
	 * Check is passenger
	 */
	@Test
	public void testIsPassenger() {
		assertFalse(ride.isPassenger());
		
		ride = new Ride(user,from,to,null,COSTS[0]);
		assertTrue(ride.isPassenger());
	}

	/**
	 * Check from getter and setter
	 */
	@Test
	public void testFrom() {
		assertEquals(from,ride.getFrom());
		
		ride.setFrom(new Location(X3,Y3));
		assertEquals(X3,ride.getFrom().x(),DELTA);
		assertEquals(Y3,ride.getFrom().y(),DELTA);
	}

	/**
	 * Check to getter and setter
	 */
	@Test
	public void testTo() {
		assertEquals(to,ride.getTo());
		
		ride.setTo(new Location(X3,Y3));
		assertEquals(X3,ride.getTo().x(),DELTA);
		assertEquals(Y3,ride.getTo().y(),DELTA);
	}

	/**
	 * Check current getter and setter
	 */
	@Test
	public void testCurrent() {
		assertEquals(from,ride.getCurrent());
		
		ride.setCurrent(new Location(X3,Y3));
		assertEquals(X3,ride.getCurrent().x(),DELTA);
		assertEquals(Y3,ride.getCurrent().y(),DELTA);
	}

	/**
	 * Check match
	 */
	@Test
	public void testMatch() {
		Ride other = new Ride(user,from,to,null,COSTS[0]);
		RideMatch someMatch = new RideMatch(ride,other);
		
		assertNull(ride.getMatch());
		ride.setMatch(someMatch);
		assertEquals(someMatch,ride.getMatch());
	}

	/**
	 * Check is matched
	 */
	@Test
	public void testIsMatched() {
		Ride other = new Ride(user,from,to,null,COSTS[0]);
		assertFalse(ride.isMatched());
		
		ride.setMatch(new RideMatch(ride,other));
		assertTrue(ride.isMatched());
	}

	/**
	 * Check coordinates from current location
	 */
	@Test
	public void testCoordinates() {
		assertEquals(X1,ride.x(),DELTA);
		assertEquals(Y1,ride.y(),DELTA);
		
		ride.setCurrent(new Location(X2,Y2));
		assertEquals(X2,ride.x(),DELTA);
		assertEquals(Y2,ride.y(),DELTA);
		
		ride.setCurrent(new Location(X3,Y3));
		assertEquals(X3,ride.x(),DELTA);
		assertEquals(Y3,ride.y(),DELTA);
	}


}
