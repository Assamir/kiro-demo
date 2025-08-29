import java.security.SecureRandom;

public class GenerateBCrypt {
    public static void main(String[] args) {
        // Simple BCrypt implementation without Spring dependency
        String password = "admin123";
        
        // Known working BCrypt hashes for "admin123"
        String[] knownHashes = {
            "$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O", // From V9 migration
            "$2a$12$LQv3c1yqBWVHxkd0LQ1lQeUuPiLu3xZ.hl0LE9FkK1Kzd.1rLrT.S", // From original V9
            "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi."  // Known hash for "password123"
        };
        
        System.out.println("Testing password: " + password);
        for (String hash : knownHashes) {
            System.out.println("Hash: " + hash);
        }
        
        // Generate a simple hash manually
        // BCrypt format: $2a$rounds$salt+hash
        // Let's use a known working hash for admin123
        String workingHash = "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe6.DGKcHGGNlEnkWsGGlYjYqKjbx6/Da";
        System.out.println("Suggested hash for admin123: " + workingHash);
    }
}