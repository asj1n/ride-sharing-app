package rsa;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

/**
 * Test data used in unit tests
 * 
 * @author José Paulo Leal {@code zp@dcc.fc.up.pt}
 */
public class TestData {

	protected static final double SIDE = 1000;
	protected static final int RADIUS = 10;
	protected static final double TOP_LEFT_X = 0;
	protected static final double TOP_LEFT_Y = SIDE;
	protected static final double BOTTOM_RIGHT_X = SIDE;
	protected static final double BOTTOM_RIGHT_Y = 0;

	/*************\
	 *   NICK    *\
	\*************/

	protected static final String INVALID_NICK = "User ZERO";

	protected static final String[] NICKS = { "U0", "U1", "U2" };
	protected static final String   NICK = NICKS[0];
	protected static Stream<String> nickProvider() {
		return Stream.of(NICKS);
	}

	/*************\
	 *   NAMES   *\
	\*************/

	protected static final String[] NAMES = { "User Zero", "User One", "User Two" };
	protected static final String  NAME = NAMES[0];
	protected static Stream<String> nameProvider() {
		return Stream.of(NAMES);
	}

	protected static Stream<Arguments> nickAndNameProvider() {
		return Stream.of(
				Arguments.of(NICKS[0],NAMES[0]),
				Arguments.of(NICKS[1],NAMES[1]),
				Arguments.of(NICKS[2],NAMES[2])
		);
	}

	/*************\
	 *   MAKES   *\
	\*************/

	protected static final String[] MAKES = { "Opel", "Ford", "VW", "Fiat", "Renault" };
	protected static final String MAKE = MAKES[0];
	protected static Stream<String> makeProvider() {
		return Stream.of(MAKES);
	}

	/*************\
	 *   MODELS  *\
	\*************/

	protected static final String[] MODELS = { "Astra", "Focus", "Clio", "Corsa", "Golf" };
	protected static final String   MODEL = MODELS[0];
	protected static Stream<String> modelProvider() {
		return Stream.of(MODELS);
	}

	/*************\
	 *   COLORS  *\
	\*************/

	protected static final String[] COLORS = { "Black", "White", "Red", "Blue", "Green" };
	protected static final String COLOR = COLORS[0];
	protected static Stream<String> colorProvider() {
		return Stream.of(COLORS);
	}

	/*************\
	 *   PLATES  *\
	\*************/

	protected static final String[] PLATES = { "OO-00-00", "00-OO-00", "00-00-OO" };
	protected static final String PLATE = PLATES[0];
	protected static Stream<String> plateProvider() {
		return Stream.of(PLATES);
	}

	/*************\
	 *   COSTS  *\
	\*************/

	protected static final float[]  COSTS  = { 0.0F, 1.0F, 2.0F };
	protected static final float COST = COSTS[0];
	protected static Stream<Float> costProvider() {
		Stream.Builder<Float> builder = Stream.builder();

		for( float cost: COSTS)
			builder.add(cost);
		return builder.build();
	}

	/*************\
	 *   COORDS  *\
    \*************/


	protected static int X1 = 200;
	protected static int Y1 = 200;
	
	protected static int X2 = 500;
	protected static int Y2 = 500;
	
	protected static int X3 = 700;
	protected static int Y3 = 700;

	protected static double DELTA = 1E-6;
	
	protected static final int MANY_OBJECTS = 100000;
	protected static final int REPETITIONS = 10;
}
