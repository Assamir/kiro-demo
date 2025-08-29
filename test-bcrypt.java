import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Generated Hash: " + hash);
        System.out.println("Verification: " + encoder.matches(password, hash));
        
        // Test some existing hashes
        String[] testHashes = {
            "$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O",
            "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.",
            "$2a$10$DowJoayNM.ING8.F8iO9T.eq8ubxcnTLIwnRcxrQwSi6tdu7h1jEm"
        };
        
        for (String testHash : testHashes) {
            System.out.println("Testing hash: " + testHash);
            System.out.println("Matches admin123: " + encoder.matches("admin123", testHash));
        }
    }
}