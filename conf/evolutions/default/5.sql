# Certifications

# --- !Ups

CREATE TABLE Certification (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  registered DATETIME NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  developer BIT(1) NOT NULL,
  organization BIT(1) NOT NULL,
  comments TEXT NOT NULL,
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE IF EXISTS Certification;