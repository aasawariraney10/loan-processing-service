CREATE TABLE IF NOT EXISTS loan_application (
    application_id UUID PRIMARY KEY,
    status VARCHAR(20),
    risk_band VARCHAR(20),
    interest_rate NUMERIC(10,2),
    emi NUMERIC(10,2),
    total_payable NUMERIC(10,2),
    rejection_reasons TEXT
);