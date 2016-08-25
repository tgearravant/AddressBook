INSERT INTO contacts (id,first_name,middle_name,last_name,birthdate,email,nickname) VALUES (1,'John','Lost','Doe',508542315874,'jdoe@gmail.com','Joe');
INSERT INTO contacts (id,first_name,middle_name,last_name,birthdate,email,nickname) VALUES (2,'Luke',null,'Skywalker',408542315874,'luke@lightside.force','Lu');
INSERT INTO contacts (id,first_name,middle_name,last_name,birthdate,email,nickname) VALUES (3,'Leia',null,'Skywalker',408542315874,'leia@rebel.scum',null);
INSERT INTO addresses (id,street,apartment,zip_code,city,state,country) VALUES (1,'600 Awesome Ave',null,'57842','Tull','AR','us');
INSERT INTO addresses (id,street,apartment,zip_code,city,state,country) VALUES (2,'600 Nonsense Ave','775','17235','Beverly','ME','ca');
INSERT INTO addresses (id,street,apartment,zip_code,city,state,country) VALUES (3,'911 Rebel Base',null,null,'Big Glacier','Hoth','ca');
INSERT INTO users (id,username,password_hash,password_salt,admin) VALUES (1,'admin','$2a$10$HsLpL8HZvDHa97LFM5Tg5uvjEdp2xmAMq2UFZyRtqeUqbsPSWZd96','$2a$10$HsLpL8HZvDHa97LFM5Tg5u',1)
INSERT INTO users (id,username,password_hash,password_salt,admin) VALUES (2,'luke','$2a$10$dRdj84vB1TxntE9HRlEONugkv05jJxctSbbj1tkbmATujWxKGdbbW','$2a$10$dRdj84vB1TxntE9HRlEONu',0)
INSERT INTO contact_addresses (id,contact_id,address_id,current_address) VALUES (1,1,1,1);
INSERT INTO contact_addresses (id,contact_id,address_id,current_address) VALUES (2,1,2,0);
INSERT INTO contact_addresses (id,contact_id,address_id,current_address) VALUES (3,2,3,0);
INSERT INTO contact_addresses (id,contact_id,address_id,current_address) VALUES (4,3,3,1);
INSERT INTO phone_numbers (id,contact_id,preferred,type,number,locale) VALUES (1,2,0,'mobile','8887774444','us');
INSERT INTO phone_numbers (id,contact_id,preferred,type,number,locale) VALUES (2,2,1,'mobile','1234567890','us');
INSERT INTO phone_numbers (id,contact_id,preferred,type,number,locale) VALUES (3,3,0,'mobile','5558675309','us');
INSERT INTO locales (id,locale,long_name) VALUES (1,'us','United States');
INSERT INTO locales (id,locale,long_name) VALUES (2,'ca','Canada');