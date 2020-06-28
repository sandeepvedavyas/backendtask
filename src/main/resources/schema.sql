drop table if exists USERS;
drop table if exists MESSAGES;

create table USERS(
  ID  SERIAL PRIMARY KEY,
  NICK_NAME varchar(1000) not null,
  PASSWORD varchar(150) not null,
  FIRST_NAME varchar(150) not null,
  LAST_NAME varchar(150) not null,
  EMAIL varchar(150) not null
);

create table MESSAGES(
  ID  SERIAL PRIMARY KEY,
  content varchar(300) not null,
  SENDER_ID int not null,
  RECEIVER_ID int not null
);