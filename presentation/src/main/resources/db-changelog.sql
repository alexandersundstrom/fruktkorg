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

--changeset fruktmannen:3
ALTER TABLE public.fruktkorg ALTER COLUMN last_changed DROP NOT NULL;

--changeset Alexander:4
CREATE TABLE reports(
    id SERIAL PRIMARY KEY NOT NULL,
    location TEXT NOT NULL,
    created TIMESTAMP DEFAULT now() NOT NULL,
    read BOOLEAN DEFAULT false  NOT NULL
);

--changeset Mio:5
ALTER TABLE reports RENAME TO report;

--changeset Mio:6
ALTER TABLE report RENAME COLUMN id TO report_id;