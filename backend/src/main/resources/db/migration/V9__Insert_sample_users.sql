-- Insert sample users for testing and demonstration
-- This migration populates the users table with sample admin and operator accounts

-- Sample Admin Users
INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at) VALUES
-- Password: admin123 (encoded with BCrypt)
('John', 'Administrator', 'admin@insurance.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: admin123 (encoded with BCrypt)
('Sarah', 'Manager', 'sarah.manager@insurance.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Operator Users
INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at) VALUES
-- Password: admin123 (encoded with BCrypt)
('Mike', 'Johnson', 'mike.johnson@insurance.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: admin123 (encoded with BCrypt)
('Lisa', 'Williams', 'lisa.williams@insurance.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: admin123 (encoded with BCrypt)
('David', 'Brown', 'david.brown@insurance.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: admin123 (encoded with BCrypt)
('Emma', 'Davis', 'emma.davis@insurance.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7Iy/Vk/uZCyqfUNNyjQinPf50lc4T.O', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Comments for documentation
COMMENT ON TABLE users IS 'System users with sample data for testing and demonstration';

-- Note: In production, these sample users should be removed or passwords should be changed
-- Default password for all test users: admin123
-- This simplifies testing while maintaining security through BCrypt encoding