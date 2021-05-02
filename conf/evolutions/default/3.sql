-- !Ups

CREATE TABLE "Users" (
  "id" INTEGER PRIMARY KEY,
  "login" VARCHAR NOT NULL,
  "password" VARCHAR NOT NULL
);

-- !Downs

DROP TABLE "Users" IF EXISTS;
