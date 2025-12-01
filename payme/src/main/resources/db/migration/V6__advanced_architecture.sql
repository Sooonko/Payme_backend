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

-- 2. Migrate data from Users to Accounts
INSERT INTO accounts (id, user_id, name, phone, updated_at)
SELECT gen_random_uuid(), id, name, phone, updated_at FROM users;

-- 3. Update Users table
ALTER TABLE users DROP COLUMN name;
ALTER TABLE users DROP COLUMN phone;
ALTER TABLE users ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

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
ALTER TABLE transactions ADD COLUMN idempotency_key VARCHAR(255);
ALTER TABLE transactions ADD CONSTRAINT uk_transactions_idempotency_key UNIQUE (idempotency_key);

-- 7. Update Audit Logs table (Change ID to BigInt)
-- Warning: This drops existing IDs. In production, you'd want a more complex migration.
ALTER TABLE audit_logs DROP CONSTRAINT audit_logs_pkey;
ALTER TABLE audit_logs DROP COLUMN id;
ALTER TABLE audit_logs ADD COLUMN id BIGSERIAL PRIMARY KEY;
