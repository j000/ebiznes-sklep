-- !Ups

CREATE TABLE "Baskets" (
  "id" INTEGER PRIMARY KEY,
  "user_id" INTEGER NOT NULL,
  "book_id" INTEGER NOT NULL,
  "count" INTEGER NOT NULL,

  FOREIGN KEY(user_id) REFERENCES Users(id),
  FOREIGN KEY(book_id) REFERENCES Books(id)
);

-- !Downs

DROP TABLE "Baskets";
