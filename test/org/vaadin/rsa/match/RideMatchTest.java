package rsa.match;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rsa.RideSharingAppException;
import rsa.TestData;
import rsa.ride.Ride;
import rsa.ride.RideRole;
import rsa.user.Car;
import rsa.user.User;
import rsa.user.Users;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static rsa.user.UsersTest.USERS_FILE;

public class RideMatchTest extends TestData {
	static Users allUsers;

	Matcher matcher;
	Location from;
	Location to;
	User driver;
	User passenger;
	Car car;
	Ride driverRide;
	Ride passengerRide;
	RideMatch match;

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

		from     = new Location(X1,Y1);
		to        = new Location(X2,Y2);
		driver    = allUsers.register(NICKS[0],NAMES[0]);
		passenger = allUsers.register(NICKS[1],NAMES[1]);
		car       = new Car(PLATES[0],MAKES[0],MODELS[0],COLORS[0]);

		driver.addCar(car);
		driverRide = new Ride(driver,from,to,PLATES[0],COSTS[0]);
	    passengerRide = new Ride(passenger,from,to,null,COSTS[0]);
		match = new RideMatch(driverRide,passengerRide);
	}

	/**
	 * Check if match was created
	 */
	@Test
	public void testRideMatch() {
		assertNotNull(match);
	}


	/**
	 * Check that matches have different IDs
	 */
	@Test
	public void testGetId() {
		Set<Long> ids = new HashSet<>();
		List<RideMatch> matches = new ArrayList<>();

		for(int i=0; i<MANY_OBJECTS; i++) {
			RideMatch match = new RideMatch(driverRide,passengerRide);

			matches.add(match); // make sure it isn't garbage collected
			ids.add(match.getId());
		}

		assertEquals(MANY_OBJECTS,ids.size());
	}

	/**
	 * Check if ride getters return the correct rides
	 */
	@Test
	public void testGetRide() {
		assertEquals(driverRide.getId(),match.getRide(RideRole.DRIVER).getId());
		assertEquals(passengerRide.getId(),match.getRide(RideRole.PASSENGER).getId());
	}

}
