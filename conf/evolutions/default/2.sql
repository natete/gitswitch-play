# --- !Ups

CREATE TABLE git_account (
  id           BIGINT AUTO_INCREMENT NOT NULL,
  username     VARCHAR(255)          NOT NULL,
  fullname     VARCHAR(255),
  email        VARCHAR(255),
  accountType  VARCHAR(50),
  photoUrl     VARCHAR(255),
  location     VARCHAR(255),
  organization VARCHAR(255),
  repoNumber   INTEGER,
  token        VARCHAR(255),
  accountId    INTEGER,
  ownerId      BIGINT                NOT NULL,

  CONSTRAINT pk_account PRIMARY KEY (id),
  CONSTRAINT uq_username UNIQUE (username, accountType),
  CONSTRAINT fk_account_user FOREIGN KEY (ownerId) REFERENCES user_account (id)
);

# --- !Downs

DROP TABLE IF EXISTS git_account;