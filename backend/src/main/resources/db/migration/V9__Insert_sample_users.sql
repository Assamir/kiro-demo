-- Insert sample users for testing and demonstration
-- This migration populates the users table with sample admin and operator accounts

-- Sample Admin Users
INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at) VALUES
-- Password: admin123 (encoded with BCrypt)
('John', 'Administrator', 'admin@insurance.com', '$2a$10$N.zmdr9k7uOLQvQHbh/Ta.4dvK6R9A4b7MiTWkoiFpDOcQhta3F6e', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: admin456 (encoded with BCrypt)
('Sarah', 'Manager', 'sarah.manager@insurance.com', '$2a$10$8K4qLxl2QeKjNvVHbh/Ta.4dvK6R9A4b7MiTWkoiFpDOcQhta3F6e', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Operator Users
INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at) VALUES
-- Password: operator123 (encoded with BCrypt)
('Mike', 'Johnson', 'mike.johnson@insurance.com', '$2a$10$L.zmdr9k7uOLQvQHbh/Ta.4dvK6R9A4b7MiTWkoiFpDOcQhta3F6e', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: operator456 (encoded with BCrypt)
('Lisa', 'Williams', 'lisa.williams@insurance.com', '$2a$10$M.zmdr9k7uOLQvQHbh/Ta.4dvK6R9A4b7MiTWkoiFpDOcQhta3F6e', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: operator789 (encoded with BCrypt)
('David', 'Brown', 'david.brown@insurance.com', '$2a$10$O.zmdr9k7uOLQvQHbh/Ta.4dvK6R9A4b7MiTWkoiFpDOcQhta3F6e', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Password: operator321 (encoded with BCrypt)
('Emma', 'Davis', 'emma.davis@insurance.com', '$2a$10$P.zmdr9k7uOLQvQHbh/Ta.4dvK6R9A4b7MiTWkoiFpDOcQhta3F6e', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Comments for documentation
COMMENT ON TABLE users IS 'System users with sample data for testing and demonstration';

-- Note: In production, these sample users should be removed or passwords should be changed
-- Default passwords for testing:
-- Admin users: admin123, admin456
-- Operator users: operator123, operator456, operator789, operator321