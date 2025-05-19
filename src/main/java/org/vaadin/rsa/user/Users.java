package org.vaadin.rsa.user;

import org.springframework.stereotype.Service;
import org.vaadin.rsa.RideSharingAppException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A collection of players. Contains methods for registration, authentication and retrieving players and their names.
 * <p>Nicks acts as keys and cannot be changed.
 * They must be a single word (no white characters) of letters, digits and underscores, starting with a letter
 * <p>Users data is serialized for persistence.
 */
@Service
public class Users implements Serializable {

    private static Users instance;
    private final HashMap<String, User> users = new HashMap<>();
    private static File file = new File("users.ser");

    /**
     * Private constructor to enforce Singleton pattern
     */
    private Users() {
    }

    /**
     * Returns the single instance of this class as proposed in the singleton design pattern.
     * If a backup of this class is available then the users instance is recreated from that data
     * @return instance of this class
     * @throws RideSharingAppException if I/O error occurs reading serialization
     */
    public static Users getInstance() throws RideSharingAppException {
        if (instance == null) {
            if (file.exists()) {
                try {
                    Users loadedUsers = loadUsersFromFile();

                    if (loadedUsers == null) {
                        instance = new Users();
                    } else {
                        instance = loadedUsers;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RideSharingAppException("Error while loading users from file", e);
                }
            } else {
                instance = new Users();
            }
        }

        return instance;
    }

    /**
     * Resets singleton for unit testing purposes.
     */
    public void reset() {
        instance = null;
        users.clear();
        file.delete();
    }

    /**
     * Name of file containing users' data
     * @return file containing serialization
     */
    public static File getUsersFile() {
        return file;
    }

    /**
     * Change pathname of file containing users' data
     * @param usersFile contain serialization
     */
    public static void setUsersFile(File usersFile) {
        file = usersFile;
    }

    /**
     * Get the user with given nick
     * @param nick of player
     * @return player instance
     */
    public User getUser(String nick) {
        return users.get(nick);
    }

    /**
     * Authenticate user with her key
     * @param nick of user
     * @param key of user
     * @return true is key if valid, false otherwise
     */
    public boolean authenticate(String nick, String key) {
        return getUser(nick) != null && getUser(nick).authenticate(key);
    }

    /**
     * Register a player with given nick and name. Changes are immediately serialized.
     * Nicks can have letters (upper and lowercase) and digits but not other characters.
     * @param nick of user
     * @param name of user
     * @return user with given nick and name, or null if nick already exists or is invalid.
     * @throws RideSharingAppException on I/O error in serialization
     */
    public User register(String nick, String name) throws RideSharingAppException {
        if (!isValidNick(nick) || getUser(nick) != null) {
            return null;
        }

        User user = new User(nick, name);
        users.put(nick, user);

        try {
            saveUsersToFile();
        } catch (IOException e) {
            throw new RideSharingAppException("Error while saving users to file", e);
        }
        return user;
    }

    /**
     * Get existing user with nick, or create one if needed. Useful for unit testing.
     * @param nick of user
     * @param name of user
     * @return user with given nick (and name)
     * @throws RideSharingAppException on serialization error.
     */
    public User getOrCreateUser(String nick, String name) throws RideSharingAppException {
        User user = getUser(nick);

        if (user == null) {
            return register(nick, name);
        }

        return user;
    }

    /**
     * Returns list of all registered Users
     * @return list of users
     */
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Checks if a given nickname is valid using a regex pattern.
     * Valid nicknames need to start with a letter and contain only letters, numbers and underscores
     * @param nick to validate
     * @return true if nickname matches criteria, false otherwise
     */
    private boolean isValidNick(String nick) {
        return nick != null && nick.matches("^[A-Za-z][A-Za-z0-9_]*$");
    }

    /**
     * Reads users serialized data from file.
     * @return Users
     * @throws IOException if an I/O error occurs during reading
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    private static Users loadUsersFromFile() throws IOException, ClassNotFoundException {
        try (ObjectInputStream fileInputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (Users) fileInputStream.readObject();
        }
    }

    /**
     * Saves users serialized data to file.
     * @throws IOException if an I/O error occurs during writing
     */
    private void saveUsersToFile() throws IOException {
        try (ObjectOutputStream fileOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            fileOutputStream.writeObject(this);
        }
    }
}
