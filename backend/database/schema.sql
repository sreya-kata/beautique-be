-- Create sequences
create sequence product_ingredients_product_ingredient_id_seq;
alter sequence product_ingredients_product_ingredient_id_seq owner to postgres;

create sequence product_concerns_product_concern_id_seq;
alter sequence product_concerns_product_concern_id_seq owner to postgres;

-- Create users table
create table users
(
    user_id    serial
        primary key,
    name       varchar(100),
    nickname   varchar(100),
    pronouns   varchar(50),
    age        integer
        constraint users_age_check
            check (age >= 0),
    gender     varchar(50),
    email      varchar(255)
        unique,
    created_at timestamp default CURRENT_TIMESTAMP
);

alter table users owner to postgres;

-- Create products table
create table products
(
    product_id          varchar(255) not null
        primary key,
    name                varchar(255),
    brand               varchar(255),
    url                 varchar(255),
    price               double precision,
    rating              double precision,
    image_url           varchar(255),
    image_alt_text      varchar(255),
    source              varchar(255),
    category            varchar(255),
    is_vegan            boolean,
    is_cruelty_free     boolean,
    is_clean_at_sephora boolean,
    created_at          timestamp default now(),
    type                varchar(255),
    suggested_usage     text
);

alter table products owner to postgres;

-- Create ingredients table
create table ingredients
(
    id               serial
        primary key,
    name             varchar(255) not null
        unique,
    category         varchar(255),
    benefit          varchar(255),
    potential_issues varchar(255)
);

alter table ingredients owner to postgres;

-- Create product_ingredients table
create table product_ingredients
(
    product_ingredient_id integer default nextval('product_ingredients_product_ingredient_id_seq'::regclass) not null
        primary key,
    product_id            varchar(255)
        references products,
    ingredient_id         integer
        references ingredients,
    constraint product_ingredients_unique
        unique (product_id, ingredient_id)
);

alter table product_ingredients owner to postgres;
alter sequence product_ingredients_product_ingredient_id_seq owned by product_ingredients.product_ingredient_id;

-- Create concerns table
create table concerns
(
    concern_id   serial
        primary key,
    concern_name varchar(255)
        unique,
    category     varchar(255)
);

alter table concerns owner to postgres;

-- Create product_concerns table
create table product_concerns
(
    product_concern_id integer default nextval('product_concerns_product_concern_id_seq'::regclass) not null
        primary key,
    product_id         varchar(255)
        references products,
    concern_id         integer
        references concerns,
    constraint product_concerns_unique
        unique (product_id, concern_id)
);

alter table product_concerns owner to postgres;
alter sequence product_concerns_product_concern_id_seq owned by product_concerns.product_concern_id;
