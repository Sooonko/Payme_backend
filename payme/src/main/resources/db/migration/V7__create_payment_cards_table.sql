-- Create payment_cards table
CREATE TABLE payment_cards (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    card_number_last4 VARCHAR(4) NOT NULL,
    card_holder_name VARCHAR(255) NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    expiry_month INTEGER NOT NULL,
    expiry_year INTEGER NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    card_token VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_card_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for faster queries
CREATE INDEX idx_payment_cards_user_id ON payment_cards(user_id);
CREATE INDEX idx_payment_cards_is_default ON payment_cards(user_id, is_default) WHERE is_default = TRUE;
CREATE INDEX idx_payment_cards_card_token ON payment_cards(card_token);

-- Add comment to explain security
COMMENT ON TABLE payment_cards IS 'Stores tokenized payment card information. NEVER store full card numbers or CVV codes.';
COMMENT ON COLUMN payment_cards.card_number_last4 IS 'Last 4 digits of card for display purposes only';
COMMENT ON COLUMN payment_cards.card_token IS 'Token from payment gateway (e.g., Stripe, QPay) representing the actual card';
