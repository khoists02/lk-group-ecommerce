ALTER TABLE products add COLUMN  price DOUBLE PRECISION NOT NULL DEFAULT 0;
ALTER TABLE products ADD COLUMN  path_image VARCHAR NULL;
ALTER TABLE products ADD COLUMN  description VARCHAR NULL;
ALTER TABLE products ADD COLUMN  sub_description VARCHAR NULL;

CREATE TABLE orders
(
    id          uuid    NOT NULL DEFAULT uuid_generate_v4(),
    quality     INT NOT NULL DEFAULT 0,
    user_id     UUID NOT NULL,
    created_at  timestamptz(0) NOT NULL DEFAULT now(),
    updated_at  timestamptz(0) NOT NULL DEFAULT now(),
    CONSTRAINT orders_pk PRIMARY KEY (id),
    CONSTRAINT orders_un UNIQUE (id, user_id)
);

ALTER TABLE orders
    ADD CONSTRAINT orders_fk_1 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE;


CREATE TABLE products_orders
(
    id         uuid           NOT NULL DEFAULT uuid_generate_v4(),
    product_id UUID           NOT NULL,
    order_id UUID           NOT NULL,
    created_at timestamptz(0) NOT NULL DEFAULT now(),
    updated_at timestamptz(0) NOT NULL DEFAULT now(),
    CONSTRAINT products_orders_pk PRIMARY KEY (id)
);


ALTER TABLE products_orders
    ADD CONSTRAINT products_orders_fk_1 FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE products_orders
    ADD CONSTRAINT role_permissions_fk_2 FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

