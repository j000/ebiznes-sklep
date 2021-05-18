-- !Ups

CREATE TABLE "Collections" (
  "id" INTEGER PRIMARY KEY,
  "name" VARCHAR NOT NULL
);

CREATE TABLE "CollectionHelpers" (
  "id" INTEGER PRIMARY KEY,
  "collection_id" INTEGER,
  "book_id" INTEGER,

  FOREIGN KEY(collection_id) REFERENCES Collections(id),
  FOREIGN KEY(book_id) REFERENCES Books(id)
);
-- !Downs

DROP TABLE "Collections_helper";
DROP TABLE "Collections";
