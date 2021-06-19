-- !Ups

ALTER TABLE "Users" RENAME TO "Users_old";

CREATE TABLE "Users" (
  "id" INTEGER PRIMARY KEY,
  "nick" TEXT,
  "provider_id" TEXT,
  "provider_key" TEXT,
  "email" TEXT
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
DROP TABLE "Users";

ALTER TABLE "Users_old" RENAME TO "Users";
