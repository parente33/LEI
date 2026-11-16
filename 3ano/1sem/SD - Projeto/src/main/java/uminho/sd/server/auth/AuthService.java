package uminho.sd.server.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Authentication service for user registration and login.
 */
public class AuthService {

    private final Map<String, String>    users;  /* username - password */
    private final ReentrantReadWriteLock lock;

    public AuthService() {
        this.users = new HashMap<>();
        this.lock  = new ReentrantReadWriteLock();
    }

    /**
     * Register a new user
     *
     * @param username User's username
     * @param password User's password
     * @return true if registration successful, false if user already exists
     */
    public boolean register(String username, String password) {
        lock.writeLock().lock();
        try {
            if (users.containsKey(username)) {
                return false;
            }
            users.put(username, password);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Authenticate a user
     *
     * @param username User's username
     * @param password User's password
     * @return {@code true} if credentials are valid, {@code false} otherwise
     */
    public boolean login(String username, String password) {
        lock.readLock().lock();
        try {
            String storedPassword = users.get(username);
            return storedPassword != null && storedPassword.equals(password);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Check if user exists.
     *
     * @return {@code true} if user exists, {@code false} otherwise
     */
    public boolean userExists(String username) {
        lock.readLock().lock();
        try {
            return users.containsKey(username);
        } finally {
            lock.readLock().unlock();
        }
    }
}
