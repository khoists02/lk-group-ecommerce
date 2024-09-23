-- Users Group --
INSERT INTO permissions
VALUES (uuid_generate_v4(), 'manageUser', 'Manage User', 'users')
ON CONFLICT (name) DO UPDATE SET name = 'manageUser',
                                 "group" = 'users';

INSERT INTO permissions
VALUES (uuid_generate_v4(), 'viewUser', 'View User', 'users')
ON CONFLICT (name) DO UPDATE SET name = 'viewUser',
                                 "group" = 'users';

-- Products --
INSERT INTO permissions
VALUES (uuid_generate_v4(), 'viewProduct', 'View Product', 'products')
ON CONFLICT (name) DO UPDATE SET name = 'viewProduct',
                                 "group" = 'products';

INSERT INTO permissions
VALUES (uuid_generate_v4(), 'manageProduct', 'Manage Product', 'products')
ON CONFLICT (name) DO UPDATE SET name = 'manageProduct',
                                 "group" = 'products';

-- Orders --
INSERT INTO permissions
VALUES (uuid_generate_v4(), 'viewOrders', 'View Orders', 'orders')
ON CONFLICT (name) DO UPDATE SET name = 'viewOrders',
                                 "group" = 'orders';

INSERT INTO permissions
VALUES (uuid_generate_v4(), 'manageOrders', 'Manage Orders', 'orders')
ON CONFLICT (name) DO UPDATE SET name = 'manageOrders',
                                 "group" = 'orders';