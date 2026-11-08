-- ===================================================================
-- Flyway Migration V1: Initial Schema Verification & System Metadata
-- ===================================================================

CREATE TABLE IF NOT EXISTS system_metadata (
    key VARCHAR(64) PRIMARY KEY,
    value VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Insert baseline health record
INSERT INTO system_metadata (key, value)
VALUES ('system_status', 'INITIALIZED')
ON CONFLICT (key) DO NOTHING;

