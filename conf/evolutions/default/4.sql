-- !Ups

CREATE TABLE "Books" (
  "id" INTEGER PRIMARY KEY,
  "title" VARCHAR NOT NULL,
  "author_id" INTEGER,
  "genre_id" INTEGER,
  "price" INTEGER,

  FOREIGN KEY(author_id) REFERENCES Authors(id),
  FOREIGN KEY(genre_id) REFERENCES Genres(id)
);

-- !Downs

DROP TABLE "Books";
