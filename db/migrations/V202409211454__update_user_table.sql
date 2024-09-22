ALTER TABLE users add COLUMN  created_at  timestamptz(0) NOT NULL DEFAULT now();
ALTER TABLE users add COLUMN  updated_at  timestamptz(0) NOT NULL DEFAULT now();