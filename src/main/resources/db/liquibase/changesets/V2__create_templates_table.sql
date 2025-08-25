--liquibase formatted sql

--changeset raii:V2__create_templates_table dbms:postgresql
CREATE TABLE templates
(
    id         UUID                        NOT NULL,
    owner_id   UUID                        NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    subject    VARCHAR(255)                NOT NULL,
    content    TEXT                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_templates PRIMARY KEY (id)
);

-- noinspection SqlResolve
ALTER TABLE claims
    ADD CONSTRAINT FK_CLAIMS_ON_TEMPLATE FOREIGN KEY (template_id) REFERENCES templates (id);
--rollback DROP TABLE templates;
