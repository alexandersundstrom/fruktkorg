--liquibase formatted sql

--changeset fruktmannen:1
CREATE TABLE fruktkorg(
  fruktkorg_id SERIAL PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE frukt(
  frukt_id SERIAL PRIMARY KEY,
  type TEXT NOT NULL,
  amount INTEGER NOT NULL,
  fruktkorg_id INTEGER REFERENCES fruktkorg(fruktkorg_id) ON DELETE CASCADE
);

--changeset fruktmannen:2
ALTER TABLE fruktkorg ADD last_changed TIMESTAMP DEFAULT now() NOT NULL;