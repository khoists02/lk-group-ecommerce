CREATE TABLE categories
(
    id          uuid    NOT NULL DEFAULT uuid_generate_v4(),
    type        VARCHAR NOT NULL,
    created_at  timestamptz(0) NOT NULL DEFAULT now(),
    updated_at  timestamptz(0) NOT NULL DEFAULT now(),
    CONSTRAINT categories_pk PRIMARY KEY (id)
);

ALTER TABLE products ADD COLUMN  category_id UUID NOT NULL;

ALTER TABLE products
    ADD CONSTRAINT product_fk_1 FOREIGN KEY (category_id) REFERENCES categories (id) ON UPDATE CASCADE;


