-- Production seed data migration
-- This migration provides minimal essential data for production deployment
-- Contains only necessary system users and basic configuration data

-- Production Admin User (should be changed after first login)
-- Default password: ChangeMe123! (must be changed in production)
INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at) VALUES
('System', 'Administrator', 'admin@company.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Production Operator User (should be changed after first login)
-- Default password: ChangeMe456! (must be changed in production)
INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at) VALUES
('System', 'Operator', 'operator@company.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add comments for production deployment notes
COMMENT ON TABLE users IS 'Production users - default passwords must be changed after deployment';

-- Production deployment checklist (as SQL comments for documentation):
-- 1. Change default admin password immediately after deployment
-- 2. Change default operator password immediately after deployment  
-- 3. Create additional users as needed through the application interface
-- 4. Verify rating table data is appropriate for production use
-- 5. Configure backup procedures for client and policy data
-- 6. Set up monitoring for the application and database
-- 7. Configure SSL certificates for secure communication
-- 8. Review and adjust logging levels for production environment
-- 9. Verify CORS settings are appropriate for production frontend URL
-- 10. Test PDF generation functionality with production data

-- Security reminder: 
-- The default passwords in this migration are for initial setup only
-- They MUST be changed before the system goes live in production