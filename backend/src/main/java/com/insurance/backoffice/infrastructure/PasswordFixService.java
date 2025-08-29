package com.insurance.backoffice.infrastructure;

import com.insurance.backoffice.domain.User;
import com.insurance.backoffice.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service to fix password hashes for existing users.
 * This service ensures all users have properly encoded BCrypt passwords.
 */
@Service
@Profile({"dev"}) // Only run in development environment
public class PasswordFixService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PasswordFixService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordFixService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting password fix process...");
        
        List<User> users = userRepository.findAll();
        boolean passwordsFixed = false;
        
        for (User user : users) {
            String currentPassword = user.getPassword();
            
            // Check if password looks like a proper BCrypt hash
            if (!currentPassword.startsWith("$2a$") || currentPassword.length() < 60) {
                logger.info("Fixing password for user: {}", user.getEmail());
                
                // Set all users to use "admin123" as the password for development
                String newPassword = passwordEncoder.encode("admin123");
                user.setPassword(newPassword);
                userRepository.save(user);
                passwordsFixed = true;
                
                logger.info("Updated password for user: {} with new BCrypt hash", user.getEmail());
            } else {
                // Test if the current hash works with "admin123"
                if (!passwordEncoder.matches("admin123", currentPassword)) {
                    logger.info("Current hash for user {} doesn't match 'admin123', updating...", user.getEmail());
                    
                    String newPassword = passwordEncoder.encode("admin123");
                    user.setPassword(newPassword);
                    userRepository.save(user);
                    passwordsFixed = true;
                    
                    logger.info("Updated password for user: {} with working BCrypt hash", user.getEmail());
                } else {
                    logger.info("Password for user {} is already correct", user.getEmail());
                }
            }
        }
        
        if (passwordsFixed) {
            logger.info("Password fix process completed - some passwords were updated");
        } else {
            logger.info("Password fix process completed - no passwords needed updating");
        }
    }
}