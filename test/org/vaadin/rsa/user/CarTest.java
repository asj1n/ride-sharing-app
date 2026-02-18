package org.vaadin.rsa.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vaadin.rsa.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests on Car, represented by plate, make, model and color.
 * This class doesn't depend on any other (rsa class) and is a good play to start.
 * 
 * @author Jos&eacute; Paulo Leal {@code jpleal@fc.up.pt}
 */
public class CarTest extends TestData {
	Car car;

	@BeforeEach
	public void setUp() throws Exception {
		car = new Car(PLATE, MAKE, MODEL, COLOR);
	}

	/**
	 * Check is car was created
	 */
	@Test
	public void testCar() {
		assertNotNull(car);
	}

	/**
	 * Check setting and getting plates
	 */
	@Test
	public void testDefaultPlate() {
		assertEquals(PLATE, car.getPlate());
	}

	/**
	 * Check setting and getting plates
	 */
	@ParameterizedTest
	@MethodSource("plateProvider")
	public void testPlate(String plate) {
		car.setPlate(plate);
		assertEquals(plate, car.getPlate());
	}

	/**
	 * test getting make assigned when car was instantiated
	 */
	@Test
	public void testDefaultMake() {
		assertEquals(MAKE, car.getMake());
	}

	/**
	 * Check setting and getting makes
	 */
	@ParameterizedTest
	@MethodSource("makeProvider")
	public void testMake(String make) {
		car.setMake(make);
		assertEquals(make, car.getMake());
	}

	/**
	 * test getting model assigned when car was instantiated
	 */
	@Test
	public void testDefaultModel() {
		assertEquals(MODEL, car.getModel());
	}

	/**
	 * Check setting and getting models
	 */
	@ParameterizedTest
	@MethodSource("modelProvider")
	public void testModel(String model) {
		car.setModel(model);
		assertEquals(model, car.getModel());
	}

	/**
	 * Test getting color assigned  when car was instanced
	 */
	@Test
	public void testGetDefaultColor() {
		assertEquals(COLOR, car.getColor());
	}

	/**
	 * Check setting and getting colors
	 */
	@ParameterizedTest
	@MethodSource("colorProvider")
	public void testSetColor(String color) {
		car.setColor(color);
		assertEquals(color,car.getColor());
	}
}
