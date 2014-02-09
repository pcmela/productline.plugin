CREATE TABLE IF NOT EXISTS productline
(
	name VARCHAR(50) NOT NULL,
	description VARCHAR(100),
	parent_productline VARCHAR(50),
	PRIMARY KEY (name)
) 
;


CREATE TABLE IF NOT EXISTS module
(
	module_id VARCHAR(50) NOT NULL,
	name VARCHAR(50),
	description VARCHAR(100),
	is_variable BOOLEAN,
	product_line_id VARCHAR(50),
	PRIMARY KEY (module_id),
	FOREIGN KEY (product_line_id) 
    		REFERENCES productline(name)
) 
;


CREATE TABLE IF NOT EXISTS package
(
	package_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	module_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (package_id)
) 
;


CREATE TABLE IF NOT EXISTS variability
(
	variability_id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	description VARCHAR(100),
	module_id VARCHAR(50) NOT NULL,
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
	module_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (element_id),
	FOREIGN KEY (module_id) 
    		REFERENCES module(module_id)
) 
;


CREATE TABLE IF NOT EXISTS type
(
	type_id INTEGER NOT NULL,
	name VARCHAR(50) NOT NULL,
	PRIMARY KEY (type_id)
) 
;


