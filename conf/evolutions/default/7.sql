-- !Ups

CREATE TABLE "Favourites" (
  "id" INTEGER PRIMARY KEY,
  "user_id" INTEGER NOT NULL,
  "book_id" INTEGER,
  "author_id" INTEGER,
  "genre_id" INTEGER,

  FOREIGN KEY(user_id) REFERENCES Users(id),
  FOREIGN KEY(book_id) REFERENCES Books(id),
  FOREIGN KEY(author_id) REFERENCES Authors(id),
  FOREIGN KEY(genre_id) REFERENCES Genres(id),

  CONSTRAINT OnlyOneColumnIsNotNull
  CHECK
  (
    ( IIF(book_id IS NULL, 0, 1)
      + IIF(author_id IS NULL, 0, 1)
      + IIF(genre_id IS NULL, 0, 1)
    ) = 1
  )
);

-- !Downs

DROP TABLE "Favourites";
