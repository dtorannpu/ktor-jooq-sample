CREATE TABLE task
(
    id          INTEGER      NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(256) NOT NULL,
    description VARCHAR(256) NOT NULL
);
