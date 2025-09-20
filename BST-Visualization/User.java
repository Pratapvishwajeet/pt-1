import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;  
  
public class User { 
    private String username;  
    private String email;  
    private String hashedPassword;  
    private long registrationDate;  
 
    public User(String username, String email, String password) {  
        this.username = username;  
        this.email = email;  
        this.hashedPassword = hashPassword(password);  
        this.registrationDate = System.currentTimeMillis();  
    }  
 
    public User(String username, String email, String hashedPassword, long registrationDate) {  
        this.username = username;  
        this.email = email;  
        this.hashedPassword = hashedPassword;  
        this.registrationDate = registrationDate;  
    }  
 
    private String hashPassword(String password) {  
        try {  
            MessageDigest md = MessageDigest.getInstance("SHA-256");  
            byte[] hashedBytes = md.digest(password.getBytes());  
            StringBuilder sb = new StringBuilder(); 
            for (byte b : hashedBytes) {  
                sb.append(String.format("%02x", b));  
            }  
            return sb.toString();  
        } catch (NoSuchAlgorithmException e) {  
            throw new RuntimeException("SHA-256 algorithm not available", e);  
        }  
    }  
 
    public boolean verifyPassword(String password) {  
        return this.hashedPassword.equals(hashPassword(password));  
    }  
 
    public String getUsername() { return username; }  
    public String getEmail() { return email; }  
    public String getHashedPassword() { return hashedPassword; }  
    public long getRegistrationDate() { return registrationDate; }  
 
    @Override  
    public String toString() {  
        return username + "," + email + "," + hashedPassword + "," + registrationDate;  
    }  
} 
