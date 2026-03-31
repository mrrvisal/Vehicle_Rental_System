package controller;

import model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling user authentication.
 * Manages user login validation and 
 * role-based access.
 */
public class LoginController {
    private List<User> users;
    
    public LoginController() {
        this.users = new ArrayList<>();
        initializeDefaultUsers();
    }
    
    /**
     * Initialize default users for testing.
     */
    private void initializeDefaultUsers() {
        // Default Admin user
        users.add(new User("admin", "admin", "Admin"));
        
        // Default Customer users
        users.add(new User("user", "user", "Customer"));
    }
    
    /**
     * Authenticate user with username and password.
     * @param username The username to validate
     * @param password The password to validate
     * @return User object if credentials are valid, null otherwise
     */
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && 
                user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Check if username exists in the system.
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Register a new customer user.
     * @param username New username
     * @param password New password
     * @return true if registration successful, false if username exists
     */
    public boolean registerCustomer(String username, String password) {
        if (usernameExists(username)) {
            return false;
        }
        users.add(new User(username, password, "Customer"));
        return true;
    }
    
    /**
     * Get all users in the system.
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Reset users to default state.
     */
    public void resetUsers() {
        users.clear();
        initializeDefaultUsers();
    }
}

