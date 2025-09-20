import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USERS_FILE = "users.dat";
    private static UserManager instance;
    private Map<String, User> users;
    private User currentUser;

    // Registration result enum
    public enum RegistrationResult {
        SUCCESS,
        USERNAME_EXISTS,
        INVALID_USERNAME,
        INVALID_EMAIL,
        WEAK_PASSWORD,
        EMPTY_FIELDS
    }

    // Login result enum
    public enum LoginResult {
        SUCCESS,
        USER_NOT_FOUND,
        INVALID_PASSWORD,
        EMPTY_FIELDS
    } 
  
    private UserManager() {  
        users = new HashMap<>();  
        loadUsers();  
    } 
  
    public static UserManager getInstance() {  
        if (instance == null) {  
            instance = new UserManager();  
        }  
        return instance;  
    } 
  
    private void loadUsers() {  
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {  
            users = (Map<String, User>) ois.readObject();  
        } catch (Exception e) {  
            users = new HashMap<>();  
        }  
    } 
  
    private void saveUsers() {  
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {  
            oos.writeObject(users);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    } 
  
    // Register user method for LoginRegisterFrame compatibility
    public RegistrationResult registerUser(String username, String password, String confirmPassword, String email, String fullName) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            return RegistrationResult.EMPTY_FIELDS;
        }

        if (users.containsKey(username.toLowerCase())) {
            return RegistrationResult.USERNAME_EXISTS;
        }

        if (password.length() < 6) {
            return RegistrationResult.WEAK_PASSWORD;
        }

        users.put(username.toLowerCase(), new User(username, email, password));
        saveUsers();
        return RegistrationResult.SUCCESS;
    }

    public LoginResult loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return LoginResult.EMPTY_FIELDS;
        }

        User user = users.get(username.toLowerCase());
        if (user == null) {
            return LoginResult.USER_NOT_FOUND;
        }

        if (!user.verifyPassword(password)) {
            return LoginResult.INVALID_PASSWORD;
        }

        currentUser = user;
        return LoginResult.SUCCESS;
    }

    public String getStorageMode() {
        return "Local Storage";
    }    public User getCurrentUser() { return currentUser; }  
    public void logout() { currentUser = null; }  
    public boolean isLoggedIn() { return currentUser != null; }  
} 
