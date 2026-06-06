CREATE TABLE IF NOT EXISTS user_push_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expo_push_token VARCHAR(255) NOT NULL,
    device_id VARCHAR(255),
    platform VARCHAR(30),
    app_version VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_seen_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    modified_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_by VARCHAR(100) NOT NULL DEFAULT 'system',
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_user_push_token UNIQUE (expo_push_token)
);

CREATE INDEX IF NOT EXISTS idx_user_push_tokens_user_enabled
    ON user_push_tokens(user_id, enabled)
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient_user_id BIGINT NOT NULL REFERENCES users(id),
    actor_user_id BIGINT REFERENCES users(id),
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body VARCHAR(500) NOT NULL,
    story_id BIGINT REFERENCES stories(id),
    story_share_id BIGINT REFERENCES story_shares(id),
    data JSONB,
    read_at TIMESTAMPTZ,
    opened_at TIMESTAMPTZ,
    push_sent_at TIMESTAMPTZ,
    push_error VARCHAR(1000),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    modified_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    modified_by VARCHAR(100) NOT NULL DEFAULT 'system',
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_notifications_recipient_created
    ON notifications(recipient_user_id, created_date DESC)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_notifications_unread
    ON notifications(recipient_user_id)
    WHERE read_at IS NULL AND deleted = FALSE;
