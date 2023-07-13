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
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users CASCADE;



-- Sequence and defined type
DROP SEQUENCE IF EXISTS posts_id_seq;
DROP SEQUENCE IF EXISTS likes_id_seq;
DROP SEQUENCE IF EXISTS comments_id_seq;
DROP SEQUENCE IF EXISTS users_id_seq;
DROP SEQUENCE IF EXISTS friends_id_seq;
DROP SEQUENCE IF EXISTS requests_id_seq;



CREATE SEQUENCE IF NOT EXISTS users_id_seq;
CREATE SEQUENCE IF NOT EXISTS posts_id_seq;
CREATE SEQUENCE IF NOT EXISTS likes_id_seq;
CREATE SEQUENCE IF NOT EXISTS comments_id_seq;
CREATE SEQUENCE IF NOT EXISTS friends_id_seq;
CREATE SEQUENCE IF NOT EXISTS requests_id_seq;



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
    image varchar DEFAULT '/static/s.png',
    PRIMARY KEY (id)
);
CREATE TABLE posts (
    id INT GENERATED ALWAYS AS IDENTITY,
    content text,
    date_Created text,
    author_name text,
    author_image text DEFAULT NULL,
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
    date_Created text,
    author_image text default NULL,
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


CREATE TABLE requests(
    id INT GENERATED ALWAYS AS IDENTITY,
    sender_id int4,
        CONSTRAINT fk_user_sender
              FOREIGN KEY(sender_id)
                  REFERENCES users(id),
    receiver_id int4,
                CONSTRAINT fk_user_receiver
                      FOREIGN KEY(receiver_id)
                           REFERENCES users(id),
    request_status boolean DEFAULT false,
    friendship_status boolean DEFAULT false,
    PRIMARY KEY (id)
);



INSERT INTO users (email,first_name,last_name ,username, image,encrypted_password)
VALUES
  ('user1@example.com','jessica','Alba','Jessica_ME','/static/12.png' ,'password1'),
  ('user2@example.com','Miranda','Prasely','MiraPrada','/static/13.png', 'password2'),
  ('user3@example.com','David', 'Jefferson', 'Lil_dave', '/static/14.png', 'password3');
-- Insert random data into the "posts" table
INSERT INTO posts (content,date_Created,author_name,author_image, user_id)
VALUES
    ('Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',  '2023-03-11 10:05','Jessica_ME','/static/12.png', 1),
    ('Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.',  '2023-07-05 01:00','MiraPrada','/static/13.png', 2),
    ('Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',  '2023-07-02 11:22','Lil_dave','/static/14.png', 3);

-- Insert random data into the "comments" table
INSERT INTO comments (comment_body, date_Created, author_name,author_image, user_id, post_id)
VALUES
    ('Modern tequila production dates back to the 1600s in Mexico, though its origins go back further to around the year 250. Today, tequila is an integral part of Mexico’s economy and cultural pride.',  '2023-07-01 12:45', 'Jessica_ME','/static/12.png', 1, 1),
    ('Tequila is a distilled spirit made from the Weber blue agave plant. Tequila is a popular spirit used in many different cocktails, like the Margarita, Paloma, and the Tequila Sunrise. ',  '2023-07-01 13:15','MiraPrada', '/static/13.png',2, 1),
    (' The Mexican government decrees that tequila is only allowed to be produced in Mexico, and only in certain designated regions, including: Jalisco, Nayarit, Guanajuato, Michoacán, and Tamaulipas',  '2023-07-03 09:00','MiraPrada','/static/13.png', 2, 3),
    (' A tequila regulatory council (the Consejo Regulador de Tequila, or CRT) maintains tequila production standards (like agave content, ABV, aging time, and ingredients) and supports the tequila industry through promoting tourism to tequila-producing regions and supporting trade with other countries.',  TIMESTAMP '2023-07-02 16:30:15','Lil_dave','/static/14.png', 3, 2);

-- Insert random data into the "likes" table
INSERT INTO likes (user_id, post_id, comment_id)
VALUES
    (1, 1, 2),
    (2, 1, 1),
    (2, 3, 2),
    (3, 2, 1);