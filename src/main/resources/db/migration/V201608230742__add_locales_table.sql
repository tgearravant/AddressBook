CREATE TABLE locales (
	id INTEGER PRIMARY KEY,
	locale VARCHAR(255) NOT NULL UNIQUE,
	long_name VARCHAR(255) NOT NULL
);

--Insert the locales that were already supported by the time this migration was created.
INSERT INTO locales (id, locale, long_name) VALUES (1,'us','United States'), (2,'ca','Canada'), (3,'nl','Netherlands');