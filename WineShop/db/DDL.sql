DROP DATABASE IF EXISTS WineShop;
CREATE DATABASE WineShop;
USE WineShop;


CREATE TABLE customer (
    id INTEGER AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
	cf VARCHAR(16) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL,
	phone VARCHAR(10) NOT NULL,
    address VARCHAR(50) NOT NULL,
    username VARCHAR(30) NOT NULL UNIQUE,
    password CHAR(50)  NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE employee (
    id INTEGER AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
	cf VARCHAR(16) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL,
	phone VARCHAR(10) NOT NULL,
    address VARCHAR(50) NOT NULL,
    username VARCHAR(30) NOT NULL UNIQUE,
    password CHAR(50)  NOT NULL,
    admin BOOLEAN,
    PRIMARY KEY (id)
);


CREATE TABLE courier (
    id INTEGER AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL,
	address VARCHAR(50) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    piva VARCHAR(11) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);


CREATE TABLE supplier (
    id INTEGER AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL,
	address VARCHAR(50) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    piva VARCHAR(11) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE wine (
    id INTEGER AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    producer VARCHAR(50) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    technical_notes VARCHAR(500) NOT NULL,
    vines VARCHAR(50) NOT NULL,
    sale_price NUMERIC(10,2) NOT NULL,
    purchase_price NUMERIC(10,2) NOT NULL,
    threshold INTEGER NOT NULL,
	id_courier INTEGER NOT NULL,
    id_supplier INTEGER NOT NULL,
	FOREIGN KEY (id_courier) REFERENCES courier (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_supplier) REFERENCES supplier (id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id)
);


CREATE TABLE itemLedgerEntry (
    id INTEGER AUTO_INCREMENT,
	id_order INTEGER NOT NULL,
	quantity INTEGER NOT NULL,
	price NUMERIC(10,2) NOT NULL,
	type VARCHAR(8) NOT NULL,
	state VARCHAR(15) NOT NULL,
    order_date TIMESTAMP NOT NULL,
	assignation_date TIMESTAMP NOT NULL,
	proposal_delivery INTEGER,
	delivery_date TIMESTAMP NULL,
	review_employee INTEGER,
	id_wine INTEGER NOT NULL,
    id_employee INTEGER,
	id_customer INTEGER,
	FOREIGN KEY (id_wine) REFERENCES wine (id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (id_employee) REFERENCES employee (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_customer) REFERENCES customer (id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id)
);
