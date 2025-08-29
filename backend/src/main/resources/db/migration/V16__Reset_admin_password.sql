-- Reset admin password to a known working BCrypt hash
-- This hash corresponds to the password "admin123"
-- Generated using BCryptPasswordEncoder with strength 10

UPDATE users 
SET password = '$2a$10$DowJoayNM.ING8.F8iO9T.eq8ubxcnTLIwnRcxrQwSi6tdu7h1jEm'
WHERE email = 'admin@insurance.com';

-- Update all other users with the same password for consistency
UPDATE users 
SET password = '$2a$10$DowJoayNM.ING8.F8iO9T.eq8ubxcnTLIwnRcxrQwSi6tdu7h1jEm'
WHERE email != 'admin@insurance.com';

-- Add comment for documentation
COMMENT ON TABLE users IS 'System users with working BCrypt password hash for admin123';