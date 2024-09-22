CREATE TABLE products (
      "id"   uuid  NOT NULL DEFAULT uuid_generate_v4(),
       productName VARCHAR NOT NULL,
      CONSTRAINT products_pk PRIMARY KEY (id)
)