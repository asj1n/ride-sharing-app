package rsa.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import rsa.TestData;
import rsa.match.PreferredMatch;
import rsa.ride.RideRole;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests on User - a participant on rides, with nick, password, cars and preferred matches.
 * This class depends on the class {@code Car} and the enums {@code RideRole} and {@code UserStars}.
 * Implement them first.
 *
 * @author Jos&eacute; Paulo Leal {@code jpleal@fc.up.pt}
 */
public class UserTest extends TestData {
	User user;

	static Stream<User> userProvider() {
		return Stream.of(
				new User(NICKS[0], NAMES[0]),
				new User(NICKS[1], NAMES[1]),
				new User(NICKS[2], NAMES[2])
		);
	}

	@BeforeEach
	public void setUp() throws Exception {
		user = new User(NICK, NAME);
	}

	/**
	 * Test if instance was created in fixture
	 */
	@Test
	public void testUser() {
		assertNotNull(user);
	}

	/**
	 * Check key generation used for authentication
	 */
	@Test
	public void testGenerateKey() {
		var key = user.generateKey();

		assertNotNull(key);
		assertEquals(key, new User(NICK, NAME).generateKey());
	}

	/**
	 * Check public method getKey() to return the generatedKey() value (package private)
	 */
	@Test
	public void testGetKey() {
		assertEquals(user.generateKey(), user.getKey());
	}

	/**
	 * Check authorization with generated key
	 * @param user to authenticate
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testAuthenticate(User user) {
		var key = user.generateKey();

		assertTrue(user.authenticate(key));
	}

	/**
	 * Check getter and setter of property nick
	 */
	@ParameterizedTest
	@MethodSource("nickAndNameProvider")
	public void testNick(String nick, String name) {
		user = new User(nick, name);
		assertEquals(nick, user.getNick());
	}

	/**
	 * Check name getter
	 */
	@ParameterizedTest
	@MethodSource("nickAndNameProvider")
	public void testGetName(String nick, String name) {
		user = new User(nick, name);
		assertEquals(name, user.getName());
	}

	/**
	 * Check name setter
	 */
	@ParameterizedTest
	@MethodSource("nameProvider")
	public void testSetName(String name) {
		user.setName(name);
		assertEquals(name, user.getName());
	}

	/**
	 * Check cars associated with user, including multiple cards,
	 * changing car features and removing cards.
	 */
	@Test
	public void testCars() {
		assertNull(user.getCar(PLATES[0]));

		Car car0 = new Car(PLATES[0], MAKES[0], MODELS[0], COLORS[0]);
		Car car1 = new Car(PLATES[1], MAKES[1], MODELS[1], COLORS[1]);

		user.addCar(car0);

		assertEquals(MAKES[0], user.getCar(PLATES[0]).getMake());
		assertNull(user.getCar(PLATES[1]));

		user.addCar(car1);

		assertEquals(MAKES[0], user.getCar(PLATES[0]).getMake());
		assertEquals(MAKES[1], user.getCar(PLATES[1]).getMake());

		assertEquals(COLORS[0], user.getCar(PLATES[0]).getColor());

		car0.setColor(COLORS[3]);

		assertEquals(COLORS[3], user.getCar(PLATES[0]).getColor());

		user.deleteCar(PLATES[0]);

		assertNull(user.getCar(PLATES[0]));
		assertEquals(MAKES[1], user.getCar(PLATES[1]).getMake());

		user.deleteCar(PLATES[1]);

		assertNull(user.getCar(PLATES[0]));
		assertNull(user.getCar(PLATES[1]));
	}

	/**
	 * Check if star average is well computed when stars are added
	 * for user in the two roles (driver and passenger)
	 */
	@Test
	public void testStars() {
		assertEquals(0, user.getAverage(RideRole.DRIVER), DELTA);
		assertEquals(0, user.getAverage(RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);

		assertEquals(4, user.getAverage(RideRole.DRIVER), DELTA);
		assertEquals(0, user.getAverage(RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FOUR_STARS, RideRole.DRIVER);

		assertEquals(4, user.getAverage(RideRole.DRIVER), DELTA);
		assertEquals(0, user.getAverage(RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FIVE_STARS, RideRole.DRIVER);

		assertEquals((4D + 4D + 5D) / 3D, user.getAverage(RideRole.DRIVER), DELTA);
		assertEquals(0, user.getAverage(RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.THREE_STARS, RideRole.DRIVER);
		user.addStars(UserStars.FIVE_STARS, RideRole.PASSENGER);

		assertEquals((4D + 4D + 5D + 3D) / 4D, user.getAverage(RideRole.DRIVER), DELTA);
		assertEquals(5, user.getAverage(RideRole.PASSENGER), DELTA);

		user.addStars(UserStars.FOUR_STARS, RideRole.PASSENGER);

		assertEquals(4.5D, user.getAverage(RideRole.PASSENGER), DELTA);
	}

	/**
	 * Tests on PreferredMatch
	 */
	@Nested
	class PreferredMatchTest {

		/**
		 * Check preferred match getter with initial value
		 */
		@Test
		public void testGetPreferredInitialDefaultValue() {
			assertEquals(PreferredMatch.BETTER, user.getPreferredMatch());
		}

		/**
		 * Check preferred match setter
		 */
		@ParameterizedTest
		@EnumSource
		public void testSetPreferred(PreferredMatch preferred) {
			user.setPreferredMatch(preferred);
			assertEquals(preferred, user.getPreferredMatch());
		}

		/**
		 * Check preferred match setter defaults to BETTER
		 */
		@Test
		public void testGetPreferredDefaultValue() {
			user.setPreferredMatch(null); // check default

			assertEquals(PreferredMatch.BETTER, user.getPreferredMatch());
		}
	}
}
