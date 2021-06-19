-- !Ups

INSERT INTO Genres
VALUES
  (1, 'Sci-Fi'),
  (2, 'Drama'),
  (3, 'Comedy'),
  (4, 'Self-help'),
  (5, 'Instructional');

INSERT INTO Authors
VALUES
  (1, 'Jan Nowak'),
  (2, 'John Smith'),
  (3, 'The Accuntant');

INSERT INTO Books
VALUES
  (1, 'Moje życie codzienne', 1, 3, 1000),
  (2, 'E-biznes', 2, 5, 4900),
  (3, 'Historia i najważniejsze fakty o liczbie 1', 3, 3, 1200),
  (4, 'Historia i najważniejsze fakty o liczbie 2', 3, 3, 1200),
  (5, 'Historia i najważniejsze fakty o liczbie 3', 3, 3, 1200),
  (6, 'Historia i najważniejsze fakty o liczbie 4', 3, 3, 1200),
  (7, 'Historia i najważniejsze fakty o liczbie 5', 3, 3, 1200),
  (8, 'Historia i najważniejsze fakty o liczbie 6', 3, 3, 1200);

-- !Downs

DELETE FROM Books
WHERE id <= 8;

DELETE FROM Authors
WHERE id <= 3;

DELETE FROM Genres
WHERE id <= 5;
