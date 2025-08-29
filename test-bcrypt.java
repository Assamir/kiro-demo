import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Matches: " + encoder.matches(password, hash));
        
        // Test the existing hash
        String existingHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O";
        System.out.println("Existing hash matches: " + encoder.matches(password, existingHash));
    }
}