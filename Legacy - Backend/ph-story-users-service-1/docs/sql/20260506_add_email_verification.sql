ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS email_verification_otp_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS email_verification_otp_expires_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS email_verification_otp_sent_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS email_verification_failed_attempts INTEGER NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_users_email_verified
    ON users(email_verified);
