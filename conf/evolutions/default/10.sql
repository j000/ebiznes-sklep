-- !Ups

CREATE TABLE "Payments" (
  "id" INTEGER PRIMARY KEY,
  "order_id" INTEGER NOT NULL,
  "amount" INTEGER,
  "comment" TEXT,

  FOREIGN KEY(order_id) REFERENCES Orders(id)
);

-- !Downs

DROP TABLE "Payments";
