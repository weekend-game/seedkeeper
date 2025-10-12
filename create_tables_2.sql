CREATE TABLE categories (
   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   numb INT,
   name CHAR(64),
   CONSTRAINT categories_pk PRIMARY KEY (id)
);

CREATE INDEX categories_idx_numb ON categories(numb);
CREATE INDEX categories_idx_name ON categories(name);

CREATE TABLE statuses (
   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   numb INT,
   name CHAR(16),
   color CHAR(6),
   CONSTRAINT statuses_pk PRIMARY KEY (id)
);

CREATE INDEX statuses_idx_numb ON statuses(numb);
CREATE INDEX statuses_idx_code ON statuses(name);

CREATE TABLE colors (
   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   name CHAR(64),
   CONSTRAINT colors_pk PRIMARY KEY (id)
);

CREATE INDEX colors_idx_code ON colors(name);

CREATE TABLE kinds (
   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   name CHAR(64),
   CONSTRAINT kinds_pk PRIMARY KEY (id)
);

CREATE INDEX kinds_idx_code ON kinds(name);
