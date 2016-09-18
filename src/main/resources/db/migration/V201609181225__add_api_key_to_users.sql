ALTER TABLE users ADD COLUMN api_key VARCHAR(100);
CREATE UNIQUE INDEX api_key_index ON users (api_key);