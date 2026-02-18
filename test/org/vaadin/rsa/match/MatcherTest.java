package org.vaadin.rsa.match;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.rsa.RideSharingAppException;
import org.vaadin.rsa.TestData;
import org.vaadin.rsa.ride.RideRole;
import org.vaadin.rsa.user.*;

import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.vaadin.rsa.user.UsersTest.USERS_FILE;

/**
 * Test a Matcher. Check if matcher matches the correct rides.
 * 
 * 
 */
public class MatcherTest extends TestData {
	static Users allUsers;

	Matcher matcher;
	Location from;
	Location to;
	Location other;
	
	@BeforeAll
	public static void prepare() throws RideSharingAppException {
		Users.setUsersFile(USERS_FILE);

		allUsers = Users.getInstance();

		Matcher.setTopLeft(new Location(TOP_LEFT_X,TOP_LEFT_Y));
		Matcher.setBottomRight(new Location(BOTTOM_RIGHT_X,BOTTOM_RIGHT_Y));
		
		Matcher.setRadius(RADIUS);
	}

	@AfterAll
	public static void tearDownClass() {
		if(USERS_FILE.exists()) {
			USERS_FILE.delete();
		}
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		allUsers.reset();

		matcher = new Matcher();
		
		from  = new Location(X1,Y1);
		to    = new Location(X2,Y2);
		other = new Location(X3,Y3);
	}
	
	/**
	 * Make a test user from standard test data 
	 * @param i indexes of users and cars
	 * @return user
	 */
	private User getUser(int i, int... js) throws RideSharingAppException {
		User user = allUsers.register(NICKS[i],NAMES[i]);
		
		for(int j: js)
			user.addCar(new Car(PLATES[j],MAKES[j], MODELS[j], COLORS[j]));
		
		return user;
	}
	
	/**
	 * Check if to top left corner location was correctly set
	 */
	@Test
	public void testTopLeft() {
		assertEquals(TOP_LEFT_X,Matcher.getTopLeft().x(),DELTA);
		assertEquals(TOP_LEFT_Y,Matcher.getTopLeft().y(),DELTA);
	}

	/**
	 * Check if to bottom right corner location was correctly set
	 */
	@Test
	public void testBottomRight() {
		assertEquals(BOTTOM_RIGHT_X,Matcher.getBottomRight().x(),DELTA);
		assertEquals(BOTTOM_RIGHT_Y,Matcher.getBottomRight().y(),DELTA);
	}

	/**
	 * Check if radius was correctly set.
	 */
	@Test
	public void testGetRadius() {
		assertEquals(RADIUS,Matcher.getRadius(),DELTA);
	}

	/**
	 * Check if rides don't match when both are drivers.
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testRidesDontMatchBothDrivers() throws RideSharingAppException {
		long driverRideId    = matcher.addRide(getUser(0), from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(getUser(1), from, to, PLATES[1],COSTS[0]);
	
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(0,driverMatches.size());
		assertEquals(0,passengerMatches.size());
	}
	
	/**
	 * Check if rides don't match when both are passengers.
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testRidesDontMatchBothPassengers() throws RideSharingAppException {
		long driverRideId    = matcher.addRide(getUser(0), from, to, null,COSTS[0]);
		long passengerRideId = matcher.addRide(getUser(1), from, to, null,COSTS[0]);
	
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(0,driverMatches.size());
		assertEquals(0,passengerMatches.size());
	}

	
	/**
	 * Check if rides don't match when destination is different.
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testRidesDontMatchDifferentDestination() throws RideSharingAppException {
		long driverRideId    = matcher.addRide(getUser(0), from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(getUser(1), from, other, null,COSTS[0]);
	
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(0,driverMatches.size());
		assertEquals(0,passengerMatches.size());
	}
	

	/**
	 * Check if rides don't match when current position is different.
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testRidesDontMatchWhenInDifferentPositions() throws RideSharingAppException {
		long driverRideId    = matcher.addRide(getUser(0), from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(getUser(1), other, to, null,COSTS[0]);
	
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, other);

		assertEquals(0,driverMatches.size());
		assertEquals(0,passengerMatches.size());
	}


	/**
	 * Simple match: both rides with same path (origin and destination).
	 * One is driver and other passenger.
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testSimpleMatch() throws RideSharingAppException {
		User driver    = getUser(0);
		User passenger = getUser(1);
		
		long driverRideId
				= matcher.addRide(driver, from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(1,driverMatches.size());
		assertEquals(1,passengerMatches.size());
		
		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[0],driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(NAMES[1],driverMatch.getName(RideRole.PASSENGER));
		assertEquals(NAMES[1],passengerMatch.getName(RideRole.PASSENGER));
		
		matcher.acceptMatch(driverRideId, passengerMatch.getId());
		matcher.acceptMatch(passengerRideId, driverMatch.getId());
		
		assertEquals(0,driver.getAverage(RideRole.DRIVER),DELTA);
		assertEquals(0,passenger.getAverage(RideRole.PASSENGER),DELTA);
		
		matcher.concludeRide(driverRideId, UserStars.FOUR_STARS);
		matcher.concludeRide(passengerRideId, UserStars.FIVE_STARS);
		
		assertEquals(5,driver.getAverage(RideRole.DRIVER),DELTA);
		assertEquals(4,passenger.getAverage(RideRole.PASSENGER),DELTA);

	}

	/**
	 * Double match: two drivers with same path (origin and destination).
	 * First has more starts and is used the default preference (BETTER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchDefault1() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,2);
		
		long driverRideId    = matcher.addRide(driver,    from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other,     from, to, PLATES[2],COSTS[0]);
		
		driver.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = matcher.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(1,driverMatches.size());
		assertEquals(2, passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[0],driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[0],driverMatch.getCar().getPlate());
		assertEquals(PLATES[0],passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination).
	 * Second has more stars and is used the default preference (BETTER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchDefault2() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,1,2);
		
		long driverRideId    = matcher.addRide(driver, from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other, from, to, PLATES[2],COSTS[0]);
		
		driver.addStars(UserStars.THREE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = matcher.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);
		
		assertEquals(1,driverMatches.size());
		assertEquals(2,passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[2],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[2],passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination).
	 * First has more starts and is used the better driver preference (BETTER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchBetter1() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,2);
		
		long driverRideId    = matcher.addRide(driver, from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other, from, to, PLATES[2],COSTS[0]);
		
		passenger.setPreferredMatch(PreferredMatch.BETTER);
		
		driver.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = matcher.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(1,driverMatches.size());
		assertEquals(2,passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[0],driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[0],driverMatch.getCar().getPlate());
		assertEquals(PLATES[0],passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination).
	 * Second has more stars and is used the better driver preference (BETTER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchBetter2() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,1,2);
		
		long driverRideId    = matcher.addRide(driver, from, to, PLATES[0],COSTS[0]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other, from, to, PLATES[2],COSTS[0]);
		
		passenger.setPreferredMatch(PreferredMatch.BETTER);
		
		driver.addStars(UserStars.THREE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches  = matcher.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);
		
		assertEquals(1,driverMatches.size());
		assertEquals(2,passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[2],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[2],passengerMatch.getCar().getPlate());
	}
	
	
	/**
	 * Double match: two drivers with same path (origin and destination).
	 * First has more starts and is used the cheapest ride preference (CHEAPER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchCheaper1() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,2);
		
		long driverRideId    = matcher.addRide(driver, from, to, PLATES[0],COSTS[1]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other, from, to, PLATES[2],COSTS[2]);
		
		passenger.setPreferredMatch(PreferredMatch.CHEAPER);
		
		driver.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = matcher.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(1,driverMatches.size());
		assertEquals(2,passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[0],driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[0],driverMatch.getCar().getPlate());
		assertEquals(PLATES[0],passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination).
	 * Second has more stars and is used the cheapest ride preference (CHEAPER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchCheaper2() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,1,2);
		
		long driverRideId    = matcher.addRide(driver, from, to, PLATES[0],COSTS[2]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other, from, to, PLATES[2],COSTS[1]);
		
		passenger.setPreferredMatch(PreferredMatch.CHEAPER);
		
		driver.addStars(UserStars.THREE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = matcher.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);
		
		assertEquals(1,driverMatches.size());
		assertEquals(2,passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[2],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[2],passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination).
	 * First has more starts and is used the closer ride preference (CLOSER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchCloser1() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,2);
		
		Location near = new Location(X1+RADIUS,Y1);
		
		long driverRideId    = matcher.addRide(driver, from, to, PLATES[0],COSTS[1]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other, from, to, PLATES[2],COSTS[2]);
		
		passenger.setPreferredMatch(PreferredMatch.CLOSER);
		
		driver.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, from);
		SortedSet<RideMatch> otherMatches     = matcher.updateRide(otherRideId, near);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);

		assertEquals(1,driverMatches.size());
		assertEquals(2,passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch driverMatch    = driverMatches.first();
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[0],driverMatch.getName(RideRole.DRIVER));
		assertEquals(NAMES[0],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[0],driverMatch.getCar().getPlate());
		assertEquals(PLATES[0],passengerMatch.getCar().getPlate());
	}
	
	/**
	 * Double match: two drivers with same path (origin and destination).
	 * Second has more stars and is used the closer ride preference (CLOSER).
	 * @throws RideSharingAppException on deserialization error.
	 */
	@Test
	public void testDoubleDriverMatchCloser2() throws RideSharingAppException {
		User driver    = getUser(0,0);
		User passenger = getUser(1);
		User other     = getUser(2,1,2);
		
		Location near = new Location(X1+RADIUS,Y1);
		
		long driverRideId    = matcher.addRide(driver, from, to, PLATES[0],COSTS[2]);
		long passengerRideId = matcher.addRide(passenger, from, to, null,COSTS[0]);
		long otherRideId     = matcher.addRide(other, from, to, PLATES[2],COSTS[1]);
		
		passenger.setPreferredMatch(PreferredMatch.CLOSER);
		
		driver.addStars(UserStars.THREE_STARS, RideRole.DRIVER);
		other.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);
		
		SortedSet<RideMatch> driverMatches    = matcher.updateRide(driverRideId, near);
		SortedSet<RideMatch> otherMatches     = matcher.updateRide(otherRideId, from);
		SortedSet<RideMatch> passengerMatches = matcher.updateRide(passengerRideId, from);
		
		assertEquals(1,driverMatches.size());
		assertEquals(2,passengerMatches.size());
		assertEquals(1,otherMatches.size());
		
		RideMatch passengerMatch = passengerMatches.first();
		
		assertEquals(NAMES[2],passengerMatch.getName(RideRole.DRIVER));
		
		assertEquals(PLATES[2],passengerMatch.getCar().getPlate());
	}
}
