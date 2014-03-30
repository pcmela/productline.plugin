CREATE TABLE IF NOT EXISTS productline
(
	productline_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	description VARCHAR(100),
	parent_productline INTEGER,
	PRIMARY KEY (productline_id)
) 
;


CREATE TABLE IF NOT EXISTS module
(
	module_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(50),
	description VARCHAR(100),
	is_variable BOOLEAN,
	product_line_id VARCHAR(50),
	PRIMARY KEY (module_id),
	FOREIGN KEY (product_line_id) 
    		REFERENCES productline(productline_id)
) 
;


CREATE TABLE IF NOT EXISTS package
(
	package_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	module_id INTEGER NOT NULL,
	PRIMARY KEY (package_id)
) 
;


CREATE TABLE IF NOT EXISTS variability
(
	variability_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	description VARCHAR(100),
	module_id INTEGER NOT NULL,
	PRIMARY KEY (variability_id),
	FOREIGN KEY (module_id) 
    		REFERENCES module(module_id)
) 
;


CREATE TABLE IF NOT EXISTS element
(
	element_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	description VARCHAR(100),
	type_id INTEGER,
	module_id INTEGER NOT NULL,
	PRIMARY KEY (element_id),
	FOREIGN KEY (module_id) 
    		REFERENCES module(module_id)
) 
;

CREATE TABLE IF NOT EXISTS resource
(
	resource_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	relative_path VARCHAR(255),
	full_path VARCHAR(500),
	element_id INTEGER NOT NULL,
	PRIMARY KEY (resource_id),
	FOREIGN KEY (element_id) 
    		REFERENCES element(element_id)
) 
;


CREATE TABLE IF NOT EXISTS type
(
	type_id INTEGER NOT NULL,
	name VARCHAR(50) NOT NULL,
	PRIMARY KEY (type_id)
) 
;

INSERT INTO type VALUES (1, 'Resources');
INSERT INTO type VALUES (2, 'Tests');
INSERT INTO type VALUES (3, 'Analysis');
INSERT INTO type VALUES (4, 'Documents');
INSERT INTO type VALUES (5, 'Diagrams');
INSERT INTO type VALUES (6, 'Others');


