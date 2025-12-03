-- 1. Create Accounts table
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(500),
    kyc_status VARCHAR(20) DEFAULT 'PENDING',
    updated_at TIMESTAMP,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Note: No data migration needed since users table never had name/phone columns

-- 4. Create External Transactions table
CREATE TABLE external_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    wallet_id UUID NOT NULL,
    provider VARCHAR(255) NOT NULL,
    external_ref_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    raw_response TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_external_transactions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_external_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

-- 5. Create Beneficiaries table
CREATE TABLE beneficiaries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    target_wallet_id UUID NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_beneficiaries_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_beneficiaries_wallet FOREIGN KEY (target_wallet_id) REFERENCES wallets(id)
);

-- 6. Update Transactions table
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(255);
CREATE UNIQUE INDEX IF NOT EXISTS uk_transactions_idempotency_key ON transactions(idempotency_key);

-- 7. Update Audit Logs table (Change ID to BigInt)
-- Create a new table with the correct structure
CREATE TABLE audit_logs_new (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID,
    action VARCHAR(255) NOT NULL,
    ip_address VARCHAR(50),
    details TEXT,
    timestamp TIMESTAMP NOT NULL
);

-- Copy data from old table to new table
INSERT INTO audit_logs_new (user_id, action, ip_address, details, timestamp)
SELECT user_id, action, ip_address, details, timestamp FROM audit_logs;

-- Drop old table and rename new table
DROP TABLE audit_logs;
ALTER TABLE audit_logs_new RENAME TO audit_logs;

-- Recreate indexes
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);

-- Add foreign key constraint
ALTER TABLE audit_logs ADD CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id);
