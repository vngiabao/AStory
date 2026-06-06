ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password_reset_otp_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS password_reset_otp_expires_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS password_reset_otp_sent_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS password_reset_failed_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS password_reset_token_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS password_reset_token_expires_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_users_password_reset_token_expires_at
    ON users(password_reset_token_expires_at);
