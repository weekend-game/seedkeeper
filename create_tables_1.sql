-- Create a database named db in the current folder:
--   java -jar "%DERBY_HOME%/lib/derbyrun.jar" ij
--   CONNECT 'jdbc:derby:db;user=user;create=true';
--   RUN 'create_tables_1.sql';
--   EXIT;

CREATE TABLE brands (
   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   name CHAR(32),
   descr VARCHAR(255),
   link VARCHAR(512),
   CONSTRAINT brands_pk PRIMARY KEY (id)
);

CREATE INDEX brands_idx_code ON brands(name);
