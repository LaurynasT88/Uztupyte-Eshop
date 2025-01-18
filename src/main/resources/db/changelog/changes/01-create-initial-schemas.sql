create table if not exists customer
(
    id         bigint auto_increment primary key,
    email      varchar(320)  not null,
    first_name varchar(255)  not null,
    last_name  varchar(255)  null,
    password   varchar(1000) not null,
    username   varchar(100)  not null,
    constraint unique_email unique (email),
    constraint unique_username unique (username)
);


create table if not exists address
(
    id             bigint auto_increment primary key,
    address_line_1 varchar(512) not null,
    address_line_2 varchar(512) null,
    city           varchar(255) not null,
    country        varchar(75)  not null,
    customer_id    bigint       null,

    constraint fk_address_customer_customer_id foreign key (customer_id) references customer (id)
);


create table if not exists product
(
    id                bigint auto_increment primary key,
    long_description varchar(255) null,
    name              varchar(255) not null,
    price             double       not null,
    short_description varchar(255) not null,
    constraint unique_product_name unique (name)
);


create table if not exists inventory
(
    id         bigint auto_increment primary key,
    product_id bigint not null,
    constraint unique_inventory_product_id unique (product_id),
    constraint fk_product_id foreign key (product_id) references product (id)
);


create table if not exists web_order
(
    id          bigint auto_increment primary key,
    address_id  bigint not null,
    customer_id bigint not null,
    constraint fk_address_address_id foreign key (address_id) references address (id),
    constraint fk_web_order_customer_customer_id foreign key (customer_id) references customer (id)
);


create table if not exists web_order_quantities
(
    id         bigint auto_increment primary key,
    quantity   int    not null,
    order_id   bigint not null,
    product_id bigint not null,
    constraint fk_web_order_order_id foreign key (order_id) references web_order (id),
    constraint fk_product_product_id foreign key (product_id) references product (id)
);


