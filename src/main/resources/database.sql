-- -------------------------------------------------------------
-- TablePlus 5.3.6(496)
--
-- https://tableplus.com/
--
-- Database: acebook_kotlin
-- Generation Time: 2023-04-25 11:31:23.3270
-- -------------------------------------------------------------


DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.
DROP TABLE IF EXISTS users;
-- Sequence and defined type
DROP SEQUENCE IF EXISTS posts_id_seq;
DROP SEQUENCE IF EXISTS likes_id_seq;
DROP SEQUENCE IF EXISTS comments_id_seq;
DROP SEQUENCE IF EXISTS users_id_seq;
CREATE SEQUENCE IF NOT EXISTS users_id_seq;
CREATE SEQUENCE IF NOT EXISTS posts_id_seq;
CREATE SEQUENCE IF NOT EXISTS likes_id_seq;
CREATE SEQUENCE IF NOT EXISTS comments_id_seq;
-- Table Definition
-- This script only contains the table creation statements and does not fully represent the table in the database. It's still missing: indices, triggers. Do not use it as a backup.
-- Sequence and defined type
-- Table Definition
CREATE TABLE users (
    id INT GENERATED ALWAYS AS IDENTITY,
    email varchar,
    encrypted_password varchar(255),
    first_name varchar DEFAULT NULL,
    last_name varchar DEFAULT NULL,
    username varchar DEFAULT NULL,
    image varchar DEFAULT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE posts (
    id INT GENERATED ALWAYS AS IDENTITY,
    content text,
    date_Created TIMESTAMP,
    author_name text,
    likes_count INT DEFAULT 0,
    post_image varchar default NULL,
    user_id int4,
       CONSTRAINT fk_user
          FOREIGN KEY(user_id)
            REFERENCES users(id),
    PRIMARY KEY (id)
);

CREATE TABLE comments(
    id INT GENERATED ALWAYS AS IDENTITY,
    comment_body text,
    date_Created TIMESTAMP,
    author_name text,
    comments_like_count INT DEFAULT 0,
    user_id int4,
           CONSTRAINT fk_user
              FOREIGN KEY(user_id)
                REFERENCES users(id),
    post_id int4,
           CONSTRAINT fk_post
              FOREIGN KEY(post_id)
                REFERENCES posts(id),
    PRIMARY KEY (id)

);

CREATE TABLE likes(
    id INT GENERATED ALWAYS AS IDENTITY,
    user_id int4,
           CONSTRAINT fk_user
              FOREIGN KEY(user_id)
                REFERENCES users(id),
    post_id int4,
           CONSTRAINT fk_post
              FOREIGN KEY(post_id)
                REFERENCES posts(id),
     comment_id int4,
               CONSTRAINT fk_comment
                  FOREIGN KEY(comment_id)
                    REFERENCES comments(id)
);
INSERT INTO users (email, encrypted_password)
VALUES
  ('user1@example.com', 'password1'),
  ('user2@example.com', 'password2'),
  ('user3@example.com', 'password3');
-- Insert random data into the "posts" table
INSERT INTO posts (content,date_Created,author_name, user_id)
VALUES
    ('Post 1 content',  TIMESTAMP'2023-07-01 12:34:56','user1@example.com', 1),
    ('Post 2 content',  TIMESTAMP'2023-07-02 10:11:12','user2@example.com', 2),
    ('Post 3 content',  TIMESTAMP'2023-07-03 08:22:33','user3@example.com', 3);

-- Insert random data into the "comments" table
INSERT INTO comments (comment_body, date_Created, author_name, user_id, post_id)
VALUES
    ('Comment 1',  TIMESTAMP'2023-07-01 12:45:00', 'user1@example.com', 1, 1),
    ('Comment 2',  TIMESTAMP'2023-07-01 13:15:30','user2@example.com', 2, 1),
    ('Comment 3',  TIMESTAMP'2023-07-03 09:00:45','user3@example.com', 2, 3),
    ('Comment 4',  TIMESTAMP '2023-07-02 16:30:15','user3@example.com', 3, 2);

-- Insert random data into the "likes" table
INSERT INTO likes (user_id, post_id, comment_id)
VALUES
    (1, 1, 2),
    (2, 1, 1),
    (2, 3, 2),
    (3, 2, 1);