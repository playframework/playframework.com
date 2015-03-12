# ModuleRelease schema
 
# --- !Ups
 
CREATE TABLE ModuleRelease (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  frameworkMatch varchar(255) DEFAULT NULL,
  isDefault bit(1) DEFAULT NULL,
  publishedDate datetime DEFAULT NULL,
  version varchar(255) DEFAULT NULL,
  module_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (module_id) REFERENCES Module (id)
) DEFAULT CHARSET=utf8;

 
# --- !Downs
 
DROP TABLE ModuleRelease;
