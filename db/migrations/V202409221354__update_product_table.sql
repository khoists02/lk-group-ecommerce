ALTER TABLE products add COLUMN  created_at  timestamptz(0) NOT NULL DEFAULT now();
ALTER TABLE products add COLUMN  updated_at  timestamptz(0) NOT NULL DEFAULT now();