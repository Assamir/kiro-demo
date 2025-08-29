-- Let's use a known working BCrypt hash for 'admin123'
-- This is generated from a reliable source
UPDATE users SET password = '$2a$10$HgfF4n0Q4USMDqsBzXMpoOg4/S57w6oCu43yHOMh13uxMs9T9CeC.' WHERE email = 'admin@insurance.com';