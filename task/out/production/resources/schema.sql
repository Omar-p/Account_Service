CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR_IGNORECASE(50)  NOT NULL PRIMARY KEY,
    password VARCHAR_IGNORECASE(500) NOT NULL,
    enabled  BOOLEAN                 NOT NULL
);


CREATE TABLE IF NOT EXISTS authorities
(
    username  VARCHAR_IGNORECASE(50) not null,
    authority VARCHAR_IGNORECASE(50) not null,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username,authority);

CREATE TABLE IF NOT EXISTS user_data
(
    id       INTEGER PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(256),
    lastname VARCHAR(256),
    email    VARCHAR_IGNORECASE(50) UNIQUE,
    CONSTRAINT fk_user_data_fk_payment_authorities FOREIGN KEY (email) REFERENCES users (username) ON DELETE CASCADE,
    CONSTRAINT fk_user_data_users FOREIGN KEY (email) REFERENCES users (username) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS payment
(
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    employee_email VARCHAR_IGNORECASE(50),
    period         VARCHAR(10),
    salary         INTEGER,
    UNIQUE (employee_email, period),
    CONSTRAINT fk_payment_user_data FOREIGN KEY (employee_email) REFERENCES user_data (email) ON DELETE CASCADE,
    CONSTRAINT EMAIL_PERIOD_UNIQUE UNIQUE (employee_email, period)
);


CREATE TABLE IF NOT EXISTS event
(
    id      INTEGER PRIMARY KEY AUTO_INCREMENT,
    date    TIMESTAMP,
    action  VARCHAR(50),
    subject VARCHAR(50),
    object  VARCHAR(50),
    path    VARCHAR(50)
);


CREATE TABLE IF NOT EXISTS failed_login_attempts
(
    email    VARCHAR_IGNORECASE(50) PRIMARY KEY,
    attempts INTEGER
);


