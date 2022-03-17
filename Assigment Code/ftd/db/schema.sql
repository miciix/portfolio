--- load with 
--- psql "dbname='webdb' user='webdbuser' password='password' host='localhost'" -f schema.sql
DROP TABLE if exists ftduser cascade;
DROP TABLE if exists score cascade;

CREATE TABLE ftduser (
	username VARCHAR(20) PRIMARY KEY,
	password BYTEA NOT NULL,
	email varchar,
	firstname VARCHAR,
	lastname varchar,
	gender varchar,
	gameage int
);

create table score(
	username varchar(20),
	easy int,
	normal int,
	hard int,
	CONSTRAINT game_fk FOREIGN KEY (username) REFERENCES ftduser(username)
);
--- Could have also stored as 128 character hex encoded values
--- select char_length(encode(sha512('abc'), 'hex')); --- returns 128
INSERT INTO ftduser VALUES('user1', sha512('password1'));
INSERT INTO ftduser VALUES('user2', sha512('password2'));
INSERT INTO ftduser VALUES('user3', sha512('password3'),'user3@mail.com','aaa','bbb','Male',10);
INSERT INTO score VALUES('user1', 0,0,0);
INSERT INTO score VALUES('user2', 0,0,0);
INSERT INTO score VALUES('user3', 0,0,0);