package rsa.ride;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests on ride roles
 * 
 */
public class RideRoleTest {

	/**
	 * Check if other in ride role enumeration
	 */
	@Test
	public void testOther() {
		assertEquals(RideRole.PASSENGER, RideRole.DRIVER.other());
		assertEquals(RideRole.DRIVER, RideRole.PASSENGER.other());
	}
}
