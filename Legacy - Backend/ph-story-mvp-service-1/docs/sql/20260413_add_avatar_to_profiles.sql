ALTER TABLE profiles
    ADD COLUMN IF NOT EXISTS avatar_id BIGINT;

ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_avatar
    FOREIGN KEY (avatar_id)
    REFERENCES media_files (id);
