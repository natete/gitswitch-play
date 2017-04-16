# --- !Ups

CREATE TABLE user_account (
  id           BIGINT AUTO_INCREMENT NOT NULL,
  username     VARCHAR(255)          NOT NULL,
  sha_password VARBINARY(64)         NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT uq_username_address UNIQUE (username)
);

# --- !Downs

DROP TABLE IF EXISTS user_account;