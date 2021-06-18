-- !Ups

ALTER TABLE "Users" RENAME TO "Users_old";

CREATE TABLE "Users" (
  "id" INTEGER PRIMARY KEY,
  "nick" TEXT,
  "provider_id" TEXT,
  "provider_key" TEXT,
  "email" TEXT
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

CREATE TABLE "PasswordInfo" (
  "id" INTEGER PRIMARY KEY,
  "providerId" TEXT,
  "providerKey" TEXT,
  "hasher" TEXT,
  "password" TEXT,
  "salt" TEXT
);

CREATE TABLE "OAuth2Info" (
  "id" INTEGER PRIMARY KEY,
  "providerId" TEXT,
  "providerKey" TEXT,
  "accessToken" TEXT,
  "tokenType" TEXT,
  "expiresIn" TEXT
);

-- !Downs

DROP TABLE "OAuth2Info";
DROP TABLE "PasswordInfo";
DROP TABLE "Passwords";
DROP TABLE "Providers";
DROP TABLE "Users";

ALTER TABLE "Users_old" RENAME TO "Users";
