CREATE TABLE authentication_key_issuers
(
    id         uuid        NOT NULL DEFAULT uuid_generate_v4(),
    issuer     varchar     NOT NULL,
    not_before timestamptz NOT NULL,
    CONSTRAINT authentication_key_issuers_pk PRIMARY KEY (id),
    CONSTRAINT authentication_key_issuers_issuer_un UNIQUE (issuer)
);