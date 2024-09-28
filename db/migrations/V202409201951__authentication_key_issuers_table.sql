CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
                       id   uuid  NOT NULL DEFAULT uuid_generate_v4(),
                       username varchar NOT NULL,
                       password varchar NOT NULL,
                       CONSTRAINT users_pk PRIMARY KEY (id)
);

ALTER TABLE users add COLUMN  created_at  timestamptz(0) NOT NULL DEFAULT now();
ALTER TABLE users add COLUMN  updated_at  timestamptz(0) NOT NULL DEFAULT now();

CREATE TABLE authentication_key_issuers
(
    id         uuid        NOT NULL DEFAULT uuid_generate_v4(),
    issuer     varchar     NOT NULL,
    not_before timestamptz NOT NULL,
    CONSTRAINT authentication_key_issuers_pk PRIMARY KEY (id),
    CONSTRAINT authentication_key_issuers_issuer_un UNIQUE (issuer)
);