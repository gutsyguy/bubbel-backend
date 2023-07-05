CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR NOT NULL UNIQUE,
  password_hash VARCHAR NOT NULL,
  email VARCHAR NOT NULL UNIQUE
);
