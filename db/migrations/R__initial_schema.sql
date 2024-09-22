CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id   uuid  NOT NULL DEFAULT uuid_generate_v4(),
    username varchar NOT NULL,
    password varchar NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (id)
)