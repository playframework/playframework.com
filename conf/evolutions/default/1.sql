# Module schema
 
# --- !Ups
 
CREATE TABLE Module (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  author varchar(255) DEFAULT NULL,
  authorId varchar(255) DEFAULT NULL,
  description longtext,
  fullname varchar(255) DEFAULT NULL,
  homepage varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8;
 
# --- !Downs
 
DROP TABLE Module;
