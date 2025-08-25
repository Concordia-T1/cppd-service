--liquibase formatted sql

--changeset raii:V1__create_cppd_table dbms:postgresql
CREATE TABLE cppd
(
    id         UUID                        NOT NULL,
    content    TEXT                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cppd PRIMARY KEY (id)
);
--rollback DROP TABLE cppd;
