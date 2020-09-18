DROP TABLE IF EXISTS public.users;
CREATE TABLE users
(
    id               serial primary key NOT NULL,
    username         varchar(20) UNIQUE NOT NULL,
    password         varchar(40)        NOT NULL,
    first_name       VARCHAR(20)        NOT NULL,
    last_name        VARCHAR(20)        NOT NULL,
    phone_number     VARCHAR(14)                ,
    email            VARCHAR(40) UNIQUE NOT NULL,
    billing_address  VARCHAR(255),
    shipping_address VARCHAR(255)
);

DROP TABLE IF EXISTS public.category;
CREATE TABLE category
(
    id          serial primary key NOT NULL,
    name        varchar(40)        NOT NULL,
    department  VARCHAR(20)        NOT NULL,
    description text               NOT NULL
);

DROP TABLE IF EXISTS public.supplier;
CREATE TABLE supplier
(
    id          serial primary key NOT NULL,
    name        varchar(40)        NOT NULL,
    description text               NOT NULL
);

DROP TABLE IF EXISTS public.product;
CREATE TABLE product
(
    id          serial primary key NOT NULL,
    name        varchar(40)        NOT NULL,
    price       FLOAT(5)           NOT NULL,
    currency    varchar(5)         not NULL,
    image       VARCHAR(100)       NOT NULL,
    description text               NOT NULL,
    supplier_id INTEGER REFERENCES supplier (id) ON DELETE CASCADE,
    category_id INTEGER REFERENCES category (id) ON DELETE CASCADE
);


DROP TABLE IF EXISTS public.cart;
CREATE TABLE cart
(
    id      serial primary key NOT NULL,
    user_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    date    timestamp          NOT NULL DEFAULT now()
);

DROP TABLE IF exists public.product_cart;
CREATE TABLE product_cart
(
    cart_id    INTEGER REFERENCES cart (id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES product (id) ON DELETE CASCADE
);

DROP TABLE IF exists public.orders;
CREATE TABLE orders
(
    id          serial primary key NOT NULL,
    date        timestamp          NOT NULL DEFAULT now(),
    cart_id     INTEGER REFERENCES cart (id) ON DELETE CASCADE,
    user_id     INTEGER REFERENCES users (id) ON DELETE CASCADE,
    status      varchar(20),
    total_price FLOAT(5)                not NULL
);

INSERT INTO supplier (name, description)
VALUES ('Amazon', 'Digital content and services.');
INSERT INTO supplier (name, description)
VALUES ('Lenovo', 'Computers');
INSERT INTO supplier (name, description)
VALUES ('Ebay', 'All you can think of');

INSERT INTO category (name, department, description)
VALUES ('Tablets', 'Hardware',
        'A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.');
INSERT INTO category (name, department, description)
VALUES ('Smartwatch', 'Wearable', 'Fitness Tracker with Heart Rate Monitor.');

INSERT INTO product (name, price, currency, image, description, supplier_id, category_id)
VALUES ('Amazon Fire', 49.9, 'USD', 'product_1.jpg', 'Fantastic price. Large content ecosystem. Good parental controls. Helpful technical support.', 1, 1);
INSERT INTO product (name, price, currency, image, description, supplier_id, category_id)
VALUES ('Lenovo IdeaPad Miix 700', 479, 'USD', 'product_2.jpg', 'Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports. Adjustable kickstand.', 2, 1);
INSERT INTO product (name, price, currency, image, description, supplier_id, category_id)
VALUES ('Amazon Fire HD 8', 89, 'USD', 'product_3.jpg', 'Latest Fire HD 8 tablet is a great value for media consumption.', 1, 1);
INSERT INTO product (name, price, currency, image, description, supplier_id, category_id)
VALUES ('Apple T80', 230, 'USD', 'product_4.jpg', 'Waterproof smartwatch with a square design with a 1.4-inch, LCD display.', 3, 2);
INSERT INTO product (name, price, currency, image, description, supplier_id, category_id)
VALUES ('Realme', 49, 'USD', 'product_5.jpg', '20 day battery life smartwatch with interchangeable straps of different colors', 3, 2);
INSERT INTO product (name, price, currency, image, description, supplier_id, category_id)
VALUES ('Sony', 69, 'USD', 'product_6.jpg', 'Sony SE20 yellow smartwatch with interchangeable strap.', 3, 2);

