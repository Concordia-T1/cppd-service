--liquibase formatted sql

--changeset raii:V3__seed_initial_cppd dbms:postgresql runOnChange:false
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM cppd WHERE id = '00000000-0000-7000-8000-000000000000';
-- noinspection SqlResolve
INSERT INTO cppd (id, content, created_at)
VALUES ('00000000-0000-7000-8000-000000000000',
        'Пример согласия на обработку персональных данных.',
        CURRENT_TIMESTAMP);
--rollback DELETE FROM cppd WHERE id = '00000000-0000-7000-8000-000000000000';
