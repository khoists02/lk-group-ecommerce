-- Users Group --
INSERT INTO permissions
VALUES (uuid_generate_v4(), 'manageUser', 'Manage User', 'users')
ON CONFLICT (name) DO UPDATE SET name = 'manageUser',
                                 "group" = 'users';

INSERT INTO permissions
VALUES (uuid_generate_v4(), 'viewUser', 'View User', 'users')
ON CONFLICT (name) DO UPDATE SET name = 'viewUser',
                                 "group" = 'users';