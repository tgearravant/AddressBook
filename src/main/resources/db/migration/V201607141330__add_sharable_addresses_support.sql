--Create contact_address Sharing Table
CREATE TABLE contact_addresses(
	id INTEGER PRIMARY KEY NOT NULL,
	contact_id INTEGER NOT NULL,
  	address_id INTEGER NOT NULL,
  	current_address BOOLEAN NOT NULL
);

-- Migrate from old method
INSERT INTO contact_addresses (contact_id,address_id,current_address)
SELECT c.id AS contact_id
	,a.id AS address_id
	,a.active AS current_address
FROM contacts AS c
INNER JOIN addresses AS a
	ON c.id=a.contact_id;

--rename old address table	
ALTER TABLE addresses RENAME TO addresses_old;

--Create new address table
CREATE TABLE addresses(
	id INTEGER PRIMARY KEY NOT NULL,
	street VARCHAR(255),
	apartment VARCHAR(255),
	zip_code VARCHAR(255),
	city VARCHAR(255),
	state VARCHAR(255),
	country VARCHAR(255)
);
-- Copy old data
INSERT INTO addresses (id,street,apartment,zip_code,city,state,country)
SELECT id,street,apartment,zip_code,city,state,country FROM addresses_old;

-- Drop old table

DROP TABLE addresses_old;