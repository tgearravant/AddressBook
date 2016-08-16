INSERT INTO contacts (id,first_name,middle_name,last_name,birthdate,email) VALUES (1,'John','Lost','Doe',508542315874,'jdoe@gmail.com');
INSERT INTO addresses (id,street,apartment,zip_code,city,state,country) VALUES (1,'600 Awesome Ave',null,'57842','Tull','AR','us');
INSERT INTO addresses (id,street,apartment,zip_code,city,state,country) VALUES (2,'600 Nonsense Ave','775','17235','Beverly','ME','ca');
INSERT INTO users (id,username,password_hash,password_salt,admin) VALUES (4,'supertull','$2a$10$bQ4/YyC/N2h0ognaSuiosukpQiQ.keaJrZZ6MmzyRlWgf2lHjOmMO','$2a$10$bQ4/YyC/N2h0ognaSuiosu',1);
INSERT INTO users (id,username,password_hash,password_salt,admin) VALUES (5,'tull','$2a$10$N1SE6YK1FLGQ/yPrFw4nz.Wai.jp9H9uvLVk4PlfaWiG0J8CvjaDC','$2a$10$N1SE6YK1FLGQ/yPrFw4nz.',0);
INSERT INTO contact_addresses (id,contact_id,address_id,current_address) VALUES (1,1,1,1);
INSERT INTO contact_addresses (id,contact_id,address_id,current_address) VALUES (2,1,2,0);