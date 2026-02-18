package rsa.match;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rsa.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test on locations, used by rides.
 * This class doesn't depend on any other (rsa class) and is a good play to start.
 *
 * @author Jos&eacute; Paulo Leal {@code jpleal@fc.up.pt}
 */
public class LocationTest extends TestData {
	Location location1, location2, location3;
	
	@BeforeEach
	public void setUp() throws Exception {
		location1 = new Location(X1,Y1);
		location2 = new Location(X2,Y2);
		location3 = new Location(X3,Y3);
	}

	/**
	 * Check X coordinate
	 */
	@Test
	public void testX() {
		assertEquals(X1,location1.x(),DELTA);
		assertEquals(X2,location2.x(),DELTA);
		assertEquals(X3,location3.x(),DELTA);
	}

	/**
	 * Check Y coordinate
	 */
	@Test
	public void testGetY() {
		assertEquals(Y1,location1.y(),DELTA);
		assertEquals(Y2,location2.y(),DELTA);
		assertEquals(Y3,location3.y(),DELTA);
	}
	
	/**
	 * Check equals (should be generated using the IDE)
	 */
	@Test
	public void testEquals() {
		assertEquals(location1,new Location(X1,Y1));
		assertEquals(location2,new Location(X2,Y2));
		assertEquals(location3,new Location(X3,Y3));
	}

}
