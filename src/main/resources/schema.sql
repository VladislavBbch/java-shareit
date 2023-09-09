DROP TABLE IF EXISTS USERS, ITEMS;

CREATE TABLE IF NOT EXISTS USERS
(
    ID    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL VARCHAR,
    NAME  VARCHAR
);

CREATE UNIQUE INDEX EMAIL_INDEX ON USERS (EMAIL);

CREATE TABLE IF NOT EXISTS ITEMS
(
    ID           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME         VARCHAR,
    DESCRIPTION  VARCHAR,
    IS_AVAILABLE BOOLEAN,
    USER_ID      INTEGER REFERENCES USERS (ID) ON DELETE CASCADE
);

