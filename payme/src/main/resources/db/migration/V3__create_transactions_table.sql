-- Create transactions table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    from_wallet_id UUID,
    to_wallet_id UUID,
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_from_wallet FOREIGN KEY (from_wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_transaction_to_wallet FOREIGN KEY (to_wallet_id) REFERENCES wallets(id)
);

-- Create indexes for faster queries
CREATE INDEX idx_transactions_from_wallet ON transactions(from_wallet_id);
CREATE INDEX idx_transactions_to_wallet ON transactions(to_wallet_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);
CREATE INDEX idx_transactions_status ON transactions(status);
