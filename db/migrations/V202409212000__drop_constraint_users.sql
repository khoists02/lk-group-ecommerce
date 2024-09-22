ALTER TABLE users DROP CONSTRAINT users_username_email_un;
ALTER TABLE users ADD CONSTRAINT users_username_email_un UNIQUE (username, email);