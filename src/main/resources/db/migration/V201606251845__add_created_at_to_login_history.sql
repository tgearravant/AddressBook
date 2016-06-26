ALTER TABLE login_attempt_history ADD COLUMN created_at INTEGER;
ALTER TABLE login_attempt_history ADD COLUMN succeeded BOOLEAN;