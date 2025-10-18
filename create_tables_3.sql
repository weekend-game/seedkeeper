CREATE TABLE Seeds (
   id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   brand_id INT,
   category_id INT,
   status_id INT,
   color_id INT,
   kind_id INT,
   mark BOOLEAN DEFAULT FALSE NOT NULL,
   name VARCHAR(255),
   article CHAR(16),
   hybrid BOOLEAN DEFAULT FALSE NOT NULL,
   use_by INT,
   description VARCHAR(4096),
   photo BLOB,
   vegetation CHAR(32),
   mass CHAR(32),
   height CHAR(32),
   yield CHAR(32),
   length CHAR(32),
   sowing_time CHAR(32),
   transplant_time CHAR(32),
   in_ground CHAR(32),
   planting_scheme CHAR(32),
   ground CHAR(3),
   CONSTRAINT seeds_pk PRIMARY KEY (id)
);

CREATE INDEX seeds_idx_brand_id ON seeds(brand_id);
CREATE INDEX seeds_idx_category_id ON seeds(category_id);
CREATE INDEX seeds_idx_status_id ON seeds(status_id);
CREATE INDEX seeds_idx_color_id ON seeds(color_id);
CREATE INDEX seeds_idx_kind_id ON seeds(kind_id);
