-- Create loan_products table
CREATE TABLE IF NOT EXISTS loan_products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    interest_rate_monthly DECIMAL(5, 2) NOT NULL,
    penalty_rate_daily DECIMAL(5, 2) NOT NULL,
    min_amount DECIMAL(19, 2) NOT NULL,
    max_amount DECIMAL(19, 2) NOT NULL,
    min_tenor_months INT NOT NULL,
    max_tenor_months INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create loan_applications table
CREATE TABLE IF NOT EXISTS loan_applications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    product_id UUID NOT NULL REFERENCES loan_products(id),
    requested_amount DECIMAL(19, 2) NOT NULL,
    max_eligible_amount DECIMAL(19, 2),
    tenor_months INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create loans table
CREATE TABLE IF NOT EXISTS loans (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL REFERENCES loan_applications(id),
    user_id UUID NOT NULL REFERENCES users(id),
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    principal_amount DECIMAL(19, 2) NOT NULL,
    remaining_balance DECIMAL(19, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    penalty_rate DECIMAL(5, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    disbursed_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create repayment_schedules table
CREATE TABLE IF NOT EXISTS repayment_schedules (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL REFERENCES loans(id),
    due_date DATE NOT NULL,
    principal_due DECIMAL(19, 2) NOT NULL,
    interest_due DECIMAL(19, 2) NOT NULL,
    total_due DECIMAL(19, 2) NOT NULL,
    principal_paid DECIMAL(19, 2) DEFAULT 0,
    interest_paid DECIMAL(19, 2) DEFAULT 0,
    penalty_paid DECIMAL(19, 2) DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create loan_transactions table
CREATE TABLE IF NOT EXISTS loan_transactions (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL REFERENCES loans(id),
    wallet_transaction_id UUID REFERENCES transactions(id),
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL
);
