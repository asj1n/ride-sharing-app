package rsa.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import rsa.TestData;
import rsa.RideSharingAppException;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests on Users - a collection of User indexed by nick
 * Depends on {@link User}, implement it first.
 *
 * @author Jos&eacute; Paulo Leal {@code jpleal@fc.up.pt}
 */
public class UsersTest extends TestData {
	public static final File USERS_FILE = new File("test_users.ser");
	static Users users;

	@BeforeAll
	public static void setUpClass() throws RideSharingAppException {
		Users.setUsersFile(USERS_FILE);

		users = Users.getInstance();
	}

	@AfterAll
	public static void tearDownClass() {
		if(USERS_FILE.exists()) USERS_FILE.delete();
	}

	@BeforeEach
	public void setUp() throws Exception {
		users.reset();

		Users.setUsersFile(USERS_FILE); // some tests change it
	}

	/**
	 * Check static getter to retrieve a default users' file
	 */
	@Test
	public void testGetUsersFile() {
		assertNotNull(Users.getUsersFile());
	}

	/**
	 * Check static getter and setter for users' file
	 */
	@ParameterizedTest
	@MethodSource("nameProvider")
	public void testSetUsersFile(String name) {
		var file = new File(name);

		Users.setUsersFile(file);
		assertEquals(file, Users.getUsersFile());
	}


	/**
	 * Test if instance was created in fixture
	 */
	@Test
	public void testGetInstance() {
		assertNotNull(users);
	}

	/**
	 * Check user registration with invalid nicks, duplicate nicks, multiple
	 * users
	 */
	@Test
	public void testRegister()  {
		assertAll(
				() -> assertNull(users.register(INVALID_NICK, NAME),"Invalid nick"),
				() -> {
					assertNotNull(users.register(NICK, NAME), "Valid nick");
					assertNull(users.register(NICK, NAME), "Duplicate nick");
				},
				() -> {
					assertNotNull(users.register(NICKS[1], NAMES[1]), "Valid nick");
					assertNull(users.register(NICKS[1], NAMES[1]), "Duplicate nick");
				}
		);
	}

	/**
	 * Check obtaining a User by nick when it is unavailable.
	 */

	@ParameterizedTest
	@MethodSource("nickProvider")
	public void testGetUser_undefined(String nick) {
		assertNull(users.getUser(nick), "Invalid nick");
	}

	/**
	 * Check obtaining a User by nick when it is available or not
	 * @throws RideSharingAppException on backup I/O errors
	 */
	@ParameterizedTest
	@MethodSource("nameProvider")
	public void testGetUser_registered(String name) throws RideSharingAppException {
		var nick = name.substring(0, 2);

		users.register(nick,name);
		assertNotNull(users.getUser(nick), "Valid nick");
	}


	/**
	 * Check authorization with generated key
	 * @param nick of user
	 * @param name of user
	 */
	@ParameterizedTest
	@MethodSource("nickAndNameProvider")
	public void testAuthenticate(String nick, String name) throws RideSharingAppException {
		var user = users.getOrCreateUser(nick,name);
		var key  = user.generateKey();
		var truncatedKey = key.substring(0, key.length()/2);

		assertAll(
				() -> assertTrue(users.authenticate(nick,key),
						"should authenticate with key"),
				() -> assertFalse(users.authenticate(nick,truncatedKey),
						"shouldn't authenticate with a truncated key"),
				() -> assertFalse(users.authenticate(nick+"2",key),
						"shouldn't authenticate with a non-existing nick")

		);
	}

	/**
	 * Class to execute tests from a different process.
	 * A different process may inicialize users from a backup, if available.
	 */
	private static class OtherTester {
		final static int OK = 0;
		final static int WRONG_NUMBER_OF_ARGUMENTS = 1;
		final static int USER_WITH_NICK_NOT_FOUND = 2;
		final static int USER_HAS_DIFFERENT_NAME = 3;
		/**
		 * Retrieve a user from a different process to test backup.
		 * Return information to calling process using exit values.
		 * @param args to retrieve user
		 * @throws RideSharingAppException on backup I/O errors
		 */
		public static void main(String[] args) throws RideSharingAppException {
			Users.setUsersFile(USERS_FILE);

			if (args.length != 2) System.exit(WRONG_NUMBER_OF_ARGUMENTS);

			Users users = Users.getInstance();
			String nick = args[0];
			String name = args[1];
			User user = users.getUser(nick);

			if (user == null) System.exit(USER_WITH_NICK_NOT_FOUND);
			if (!name.equals(user.getName())) System.exit(USER_HAS_DIFFERENT_NAME);

			System.exit(OK);

		}

		/**
		 * Execute the main in this class, for tests that must
		 * be executed from a different process.
		 *
		 * @param nick of user to test
		 * @param name of user to test
		 * @return process exit value
		 * @throws IOException on process execution
		 * @throws InterruptedException on waiting for process termination
		 */
		private static int execute(String nick, String name) throws IOException, InterruptedException {
			var runtime = Runtime.getRuntime();
			var className = OtherTester.class.getName();
			var javaHome = System.getProperty("java.home");
			var classPath = System.getProperty("java.class.path");
			var separator = System.getProperty("file.separator");
			var javaPath = javaHome + separator + "bin" + separator + "java";
			var commandLine = new String[] {javaPath, "-cp", classPath, className, nick, name};
			var process = runtime.exec(commandLine);

			show(process.errorReader());
			show(process.inputReader());

			return process.waitFor();
		}

		/**
		 * Show process readers (for debugging)
		 * @param reader providing characters to read
		 * @throws IOException reading stream
		 */
		private static void show(Reader reader) throws IOException {
			try(var bufferedReader = new BufferedReader(reader)) {
				bufferedReader.lines().forEach(System.out::println);
			}
		}
	}

	/**
	 * Checks a user on the singleton backup.
	 * Registers a user and launches a different process to check if it was correctly recorded.
	 *
	 * @param nick of user
	 * @param name of user
	 * @throws RideSharingAppException if register fails to load backup
	 */
	@ParameterizedTest
	@MethodSource("nickAndNameProvider")
	public void testBackup(String nick,String name) throws RideSharingAppException {
		users.register(nick,name);

		assertAll(
				() -> assertEquals(OtherTester.OK,OtherTester.execute(nick,name) ,
						"user should be on the backup"),
				() -> assertEquals(OtherTester.USER_WITH_NICK_NOT_FOUND,OtherTester.execute(nick+"2",""),
						"wrong nick, shouldn't be on the backup"),
				() -> assertEquals(OtherTester.USER_HAS_DIFFERENT_NAME,OtherTester.execute(nick,name+"2"),
						"wrong name, shouldn't be on the backup"),
				() -> {
					USERS_FILE.delete();
					assertEquals(OtherTester.USER_WITH_NICK_NOT_FOUND,OtherTester.execute(nick,name) ,
				"user should not be on backup after deleting file");
				}
		);
	}

}
