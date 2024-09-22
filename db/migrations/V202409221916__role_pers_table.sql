CREATE TABLE permissions
(
    id          uuid    NOT NULL DEFAULT uuid_generate_v4(),
    name        varchar NOT NULL,
    description varchar NOT NULL,
    "group"   varchar NOT NULL,
    created_at  timestamptz(0) NOT NULL DEFAULT now(),
    updated_at  timestamptz(0) NOT NULL DEFAULT now(),
    CONSTRAINT permissions_pk PRIMARY KEY (id),
    CONSTRAINT permissions_un UNIQUE (name)
);

CREATE TABLE roles
(
    id          uuid    NOT NULL DEFAULT uuid_generate_v4(),
    "name"      varchar NOT NULL,
    description varchar NULL,
    created_at  timestamptz(0) NOT NULL DEFAULT now(),
    updated_at  timestamptz(0) NOT NULL DEFAULT now(),
    CONSTRAINT roles_pk PRIMARY KEY (id)
);

CREATE TABLE role_permissions
(
    id            uuid NOT NULL,
    role_id       uuid NOT NULL,
    permission_id uuid NOT NULL,
    created_at  timestamptz(0) NOT NULL DEFAULT now(),
    updated_at  timestamptz(0) NOT NULL DEFAULT now(),
    CONSTRAINT role_permissions_pk PRIMARY KEY (id)
);

ALTER TABLE role_permissions
    ADD CONSTRAINT role_permissions_fk_1 FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE role_permissions
    ADD CONSTRAINT role_permissions_fk_2 FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE TABLE user_roles
(
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    created_at  timestamptz(0) NOT NULL DEFAULT now(),
    updated_at  timestamptz(0) NOT NULL DEFAULT now(),
    CONSTRAINT user_roles_pk PRIMARY KEY (user_id, role_id)
);

ALTER TABLE user_roles
    ADD CONSTRAINT user_roles_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE user_roles
    ADD CONSTRAINT user_roles_fk_1 FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE ON UPDATE CASCADE;