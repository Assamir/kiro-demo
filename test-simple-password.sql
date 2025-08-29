-- Set a simple password hash that we know works
-- This is BCrypt hash for "test123" with strength 10
UPDATE users SET password = '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe6.DGKcHGGNlEnkWsGGlYjYqKjbx6/Da' WHERE email = 'admin@insurance.com';