-- Fix user passwords with correct BCrypt hash for 'admin123'
-- This migration updates all existing users with the correct password hash

UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O' 
WHERE password = '$2a$12$LQv3c1yqBWVHxkd0LQ1lQeUuPiLu3xZ.hl0LE9FkK1Kzd.1rLrT.S';

-- Add comment for documentation
COMMENT ON TABLE users IS 'System users with corrected BCrypt password hashes for admin123';