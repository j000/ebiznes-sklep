-- !Ups

ALTER TABLE "Users" RENAME TO "Users_old";

CREATE TABLE "Users" (
  "id" INTEGER PRIMARY KEY,
  "nick" TEXT
);

CREATE TABLE "Providers" (
  "id" INTEGER PRIMARY KEY,
  "user_id" INTEGER NOT NULL,
  "provider_id" TEXT,
  "provider_key" TEXT,

  FOREIGN KEY(user_id) REFERENCES Users(id)
);

CREATE TABLE "Passwords" (
  "id" INTEGER PRIMARY KEY,
  "provider_id" INTEGER NOT NULL,
  "hasher" TEXT,
  "password" TEXT,
  "salt" TEXT,

  FOREIGN KEY(provider_id) REFERENCES Providers(id)
);

-- !Downs

DROP TABLE "Passwords";
DROP TABLE "Providers";
DROP TABLE "Users";

ALTER TABLE "Users_old" RENAME TO "Users";
