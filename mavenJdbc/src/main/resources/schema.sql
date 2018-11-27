--drop table Role;
--drop table User;

CREATE TABLE IF NOT EXISTS role(
      id BIGINT AUTO_INCREMENT primary key,
       name varchar(255));
CREATE TABLE IF NOT EXISTS user(id BIGINT primary key AUTO_INCREMENT, login varchar(255), password varchar(255),
      email varchar(255), first_name varchar(255), last_name varchar(255), birthday DATE, role_id BIGINT);