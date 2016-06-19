CREATE TABLE addresses(
	id INTEGER PRIMARY KEY NOT NULL,
	contact_id INTEGER NOT NULL,
	active BOOLEAN NOT NULL,
	street VARCHAR(255),
	apartment VARCHAR(255),
	zip_code VARCHAR(255),
	city VARCHAR(255),
	state VARCHAR(255),
	country VARCHAR(255)
)