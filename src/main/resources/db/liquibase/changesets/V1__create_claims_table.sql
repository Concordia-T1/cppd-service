--liquibase formatted sql

--changeset raii:V1__create_claims_table dbms:postgresql
CREATE TABLE claims
(
    id                    UUID                        NOT NULL,
    owner_id              UUID                        NOT NULL,
    owner_email           VARCHAR(255)                NOT NULL,
    candidate_email       VARCHAR(255)                NOT NULL,
    candidate_last_name   VARCHAR(255),
    candidate_first_name  VARCHAR(255),
    candidate_middle_name VARCHAR(255),
    candidate_birthdate date,
    candidate_phone       VARCHAR(255),
    template_id           UUID                        NOT NULL,
    status                VARCHAR(255)                NOT NULL,
    responded_at          TIMESTAMP WITHOUT TIME ZONE,
    expires_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_claims PRIMARY KEY (id)
);
--rollback DROP TABLE claims;
