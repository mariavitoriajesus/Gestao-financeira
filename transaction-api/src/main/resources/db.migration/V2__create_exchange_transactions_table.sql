CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE exchange_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID NOT NULL,
    currency_from VARCHAR(3) NOT NULL,
    currency_to VARCHAR(3) NOT NULL,
    rate NUMERIC(19, 8) NOT NULL,
    amount_from NUMERIC(19, 2) NOT NULL,
    amount_to NUMERIC(19, 2) NOT NULL,
    quote_date DATE NOT NULL,
    source VARCHAR(30) NOT NULL,

    CONSTRAINT fk_exchange_transactions_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transactions (id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX ux_exchange_transactions_transaction_id
    ON exchange_transactions (transaction_id);

CREATE INDEX idx_exchange_transactions_pair_date
    ON exchange_transactions (currency_from, currency_to, quote_date);
