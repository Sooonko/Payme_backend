-- Add scoring_multiplier to loan_products
ALTER TABLE loan_products ADD COLUMN IF NOT EXISTS scoring_multiplier DECIMAL(5, 2) DEFAULT 1.0;
UPDATE loan_products SET scoring_multiplier = 1.0 WHERE scoring_multiplier IS NULL;
ALTER TABLE loan_products ALTER COLUMN scoring_multiplier SET NOT NULL;

-- Create loan_eligibilities table
CREATE TABLE IF NOT EXISTS loan_eligibilities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    product_id UUID NOT NULL REFERENCES loan_products(id),
    max_eligible_amount DECIMAL(19, 2) NOT NULL,
    status_message VARCHAR(255) NOT NULL,
    checked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_product_eligibility UNIQUE (user_id, product_id)
);
