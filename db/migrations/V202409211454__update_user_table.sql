ALTER TABLE users ADD COLUMN IF NOT EXISTS  created_at  timestamptz(0) NOT NULL DEFAULT now();
ALTER TABLE users ADD COLUMN IF NOT EXISTS  updated_at  timestamptz(0) NOT NULL DEFAULT now();