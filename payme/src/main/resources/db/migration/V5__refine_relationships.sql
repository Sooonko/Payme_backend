ALTER TABLE audit_logs DROP COLUMN user_id;
ALTER TABLE audit_logs ADD COLUMN user_id UUID;
ALTER TABLE audit_logs ADD CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE wallets DROP CONSTRAINT IF EXISTS wallets_user_id_key;
ALTER TABLE wallets ADD CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users(id);
