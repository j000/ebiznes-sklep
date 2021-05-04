-- !Ups

CREATE TABLE "Reviews" (
  "id" INTEGER PRIMARY KEY,
  "content" VARCHAR NOT NULL,
  "user_id" INTEGER,
  "book_id" INTEGER,

  FOREIGN KEY(user_id) REFERENCES Users(id),
  FOREIGN KEY(book_id) REFERENCES Books(id)
);

-- !Downs

DROP TABLE "Reviews";
