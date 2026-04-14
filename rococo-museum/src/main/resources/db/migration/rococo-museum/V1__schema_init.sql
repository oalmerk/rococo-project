-- Включение расширения для UUID (если еще не включено)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Создание таблицы country
CREATE TABLE IF NOT EXISTS country
(
    id   UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

-- Создание таблицы museum
CREATE TABLE IF NOT EXISTS museum
(
    id          UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    title       VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(1000),
    city        VARCHAR(255),
    photo       BYTEA,
    country_id  UUID NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country (id)
);