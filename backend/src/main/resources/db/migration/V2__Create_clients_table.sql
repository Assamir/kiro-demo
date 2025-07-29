-- Create clients table
-- This table stores client personal information required for policy issuance

CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    pesel VARCHAR(11) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL
);

-- Create indexes for performance and data integrity
CREATE UNIQUE INDEX idx_clients_pesel ON clients(pesel);
CREATE INDEX idx_clients_full_name ON clients(full_name);
CREATE INDEX idx_clients_email ON clients(email);

-- Add constraints for data validation
ALTER TABLE clients ADD CONSTRAINT chk_clients_pesel_length 
    CHECK (LENGTH(pesel) = 11 AND pesel ~ '^[0-9]+$');
ALTER TABLE clients ADD CONSTRAINT chk_clients_email_format 
    CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
ALTER TABLE clients ADD CONSTRAINT chk_clients_full_name_not_empty 
    CHECK (LENGTH(TRIM(full_name)) > 0);

-- Add comments for documentation
COMMENT ON TABLE clients IS 'Client personal information for policy issuance';
COMMENT ON COLUMN clients.pesel IS 'Polish national identification number (11 digits)';
COMMENT ON COLUMN clients.full_name IS 'Client full name as it appears on official documents';
COMMENT ON COLUMN clients.address IS 'Complete client address for correspondence';