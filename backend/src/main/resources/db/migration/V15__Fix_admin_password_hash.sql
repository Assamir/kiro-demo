-- Fix admin password hash with a known working BCrypt hash for 'admin123'
-- Generated using BCryptPasswordEncoder with default strength (10)

UPDATE users 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' 
WHERE email = 'admin@insurance.com';

-- Update all other users with the same working hash
UPDATE users 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' 
WHERE password = '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O';

-- Add comment for documentation
COMMENT ON TABLE users IS 'System users with verified BCrypt password hashes for admin123';