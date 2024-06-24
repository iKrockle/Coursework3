-- liquibase formatted sql

-- changeset Krockle:1
CREATE TABLE public.notification_task
(
    id bigint NOT NULL,
    chat_id bigint NOT NULL,
    notification_date date NOT NULL,
    CONSTRAINT pk_notification_task PRIMARY KEY (id)
);

-- changeset Krockle:2
ALTER TABLE IF EXISTS public.notification_task
    ADD COLUMN notification_message text;

-- changeset Krockle:3
ALTER TABLE public.notification_task
    ALTER COLUMN notification_date TYPE timestamp without time zone;