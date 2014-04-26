\c askme;

BEGIN;

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL CONSTRAINT users_pk PRIMARY KEY,
  first_name varchar(30),
  last_name varchar(30),
  email varchar(50) NOT NULL,
  username varchar(50) NOT NULL,
  is_admin boolean DEFAULT false,
  last_login timestamptz,
  creation_date timestamptz DEFAULT now(),
  is_active boolean DEFAULT true,
  password varchar(100)
);


CREATE UNIQUE INDEX users_email ON users (lower(email));
CREATE UNIQUE INDEX users_username ON users (lower(username));

-- QUESTIONS TABLE
CREATE TABLE IF NOT EXISTS questions (
  id BIGSERIAL CONSTRAINT questions_id PRIMARY KEY,
  question TEXT,
  users_id INTEGER REFERENCES users(id),
  timestamp timestamptz DEFAULT now()
  );

CREATE INDEX  question_timestamp_idx ON questions (timestamp);
CREATE INDEX  question_author_idx ON questions (users_id);

-- ANSWERS TABLE
CREATE TABLE IF NOT EXISTS answers (
  id BIGSERIAL CONSTRAINT answers_id PRIMARY KEY,
  answer TEXT,
  users_id INTEGER REFERENCES users(id),
  questions_id INTEGER REFERENCES questions(id),
  timestamp timestamptz DEFAULT now()
  );

CREATE INDEX answers_timestamp_idx ON answers (timestamp);
CREATE INDEX answers_author_idx ON answers (users_id);

COMMIT;
