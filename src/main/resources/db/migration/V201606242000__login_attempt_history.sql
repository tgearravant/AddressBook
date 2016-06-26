CREATE TABLE login_attempt_history(
	id INTEGER PRIMARY KEY NOT NULL,
	username VARCHAR(255) NOT NULL,
  ip_address VARCHAR(255) NOT NULL,
  user_agent VARCHAR(255) NOT NULL
)