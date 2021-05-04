-- !Ups

CREATE TABLE "Orders" (
  "id" INTEGER PRIMARY KEY,
  "user_id" INTEGER NOT NULL,

  FOREIGN KEY(user_id) REFERENCES Users(id)
);

CREATE TABLE "Orders_helper" (
  "id" INTEGER PRIMARY KEY,
  "order_id" INTEGER,
  "book_id" INTEGER,
  "price" INTEGER,
  "count" INTEGER,

  FOREIGN KEY(order_id) REFERENCES Orders(id),
  FOREIGN KEY(book_id) REFERENCES Books(id)
);
-- !Downs

DROP TABLE "Orders_helper";
DROP TABLE "Orders";
