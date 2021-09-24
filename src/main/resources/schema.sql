DROP TABLE IF EXISTS item;

CREATE TABLE users  (
    firstname VARCHAR(40),
    phone VARCHAR(40)
);

CREATE TABLE batch_percentage  (
    id serial primary key,
    batch_id VARCHAR(100),
    percentage integer (100)
);