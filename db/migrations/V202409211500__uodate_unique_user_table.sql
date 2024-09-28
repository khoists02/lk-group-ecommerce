ALTER TABLE users ADD COLUMN IF NOT EXISTS  email VARCHAR NULL;
ALTER TABLE users ADD CONSTRAINT users_username_email_un UNIQUE (id, username, email)