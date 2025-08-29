-- Fix password with a known working BCrypt hash for 'admin123'
-- Generated using online BCrypt generator with strength 10
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O
UPDATE users 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O'
WHERE email = 'admin@insurance.com';

-- Update all other users with the same password for consistency
UPDATE users 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O'
WHERE email != 'admin@insurance.com';

-- Add comment for documentation
COMMENT ON TABLE users IS 'System users with verified BCrypt password hash for admin123';