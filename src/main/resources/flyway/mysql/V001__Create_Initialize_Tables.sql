CREATE TABLE author
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

CREATE TABLE keyword
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

CREATE TABLE language
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

CREATE TABLE book
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(255),
    full_title   VARCHAR(255),
    description  TEXT,
    publisher    VARCHAR(255),
    isbn         VARCHAR(255),
    cover        VARCHAR(255),
    publish_year INT,
    pages        INT,
    barcode      VARCHAR(255),
    status       VARCHAR(255)
);
