-- !Ups

CREATE TABLE "Authors" (
  "id" INTEGER PRIMARY KEY,
  "name" VARCHAR NOT NULL
);

-- !Downs

DROP TABLE "Authors" IF EXISTS;
