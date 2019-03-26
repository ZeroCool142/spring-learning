-- Drop table

-- DROP TABLE learnspring.audit

CREATE TABLE audit (
	audit_id serial NOT NULL,
	organization_id int4 NULL,
	contract_id int4 NULL,
	contract_amount numeric(9,2) NULL,
	CONSTRAINT audit_pkey PRIMARY KEY (audit_id),
	CONSTRAINT contract_fk FOREIGN KEY (contract_id) REFERENCES contract(contract_id),
	CONSTRAINT organization_fk FOREIGN KEY (organization_id) REFERENCES organization(organization_id)
);

-- Drop table

-- DROP TABLE city

CREATE TABLE city (
	city_id serial NOT NULL,
	"name" text NOT NULL,
	CONSTRAINT city_name_key UNIQUE (name),
	CONSTRAINT city_pk PRIMARY KEY (city_id)
);

-- Drop table

-- DROP TABLE contract

CREATE TABLE contract (
	contract_id serial NOT NULL,
	amount numeric NOT NULL,
	organization_id int4 NOT NULL,
	contract_date date NOT NULL DEFAULT 'now'::text::date,
	CONSTRAINT contract_pk PRIMARY KEY (contract_id),
	CONSTRAINT contract_fk0 FOREIGN KEY (organization_id) REFERENCES organization(organization_id)
);

-- Drop table

-- DROP TABLE "location"

CREATE TABLE "location" (
	location_id serial NOT NULL,
	city_id int4 NOT NULL,
	address text NOT NULL,
	CONSTRAINT location_pk PRIMARY KEY (location_id),
	CONSTRAINT location_fk0 FOREIGN KEY (city_id) REFERENCES city(city_id)
);

-- Drop table

-- DROP TABLE organization

CREATE TABLE organization (
	organization_id serial NOT NULL,
	"name" text NOT NULL,
	CONSTRAINT organization_name_key UNIQUE (name),
	CONSTRAINT organization_pk PRIMARY KEY (organization_id)
);

-- Drop table

-- DROP TABLE payment

CREATE TABLE payment (
	payment_id serial NOT NULL,
	"type" text NOT NULL,
	payment_date date NOT NULL,
	amount numeric NOT NULL,
	contract_id int4 NOT NULL,
	CONSTRAINT big_payment CHECK ((amount < (500)::numeric)),
	CONSTRAINT payment_pk PRIMARY KEY (payment_id),
	CONSTRAINT payment_fk0 FOREIGN KEY (contract_id) REFERENCES contract(contract_id)
);

-- Drop table

-- DROP TABLE product

CREATE TABLE product (
	product_id serial NOT NULL,
	"name" text NOT NULL,
	CONSTRAINT product_name_key UNIQUE (name),
	CONSTRAINT product_pk PRIMARY KEY (product_id)
);

-- Drop table

-- DROP TABLE shipment

CREATE TABLE shipment (
	shipment_id serial NOT NULL,
	location_id int4 NOT NULL,
	expected_date_shipment date NOT NULL,
	expected_date_arrival date NOT NULL,
	actual_date_shipment date NULL,
	actual_date_arrival date NULL,
	contract_id int8 NOT NULL,
	CONSTRAINT shipment_pk PRIMARY KEY (shipment_id),
	CONSTRAINT shipment_contract_fk FOREIGN KEY (contract_id) REFERENCES contract(contract_id),
	CONSTRAINT shipment_fk0 FOREIGN KEY (location_id) REFERENCES location(location_id)
);

-- Drop table

-- DROP TABLE shipment_product

CREATE TABLE shipment_product (
	shipment_product_id serial NOT NULL,
	shipment_id int4 NOT NULL,
	product_id int4 NOT NULL,
	amount int4 NOT NULL DEFAULT 1,
	CONSTRAINT shipment_product_pk PRIMARY KEY (shipment_product_id),
	CONSTRAINT shipment_product_fk0 FOREIGN KEY (shipment_id) REFERENCES shipment(shipment_id),
	CONSTRAINT shipment_product_fk1 FOREIGN KEY (product_id) REFERENCES product(product_id)
);
