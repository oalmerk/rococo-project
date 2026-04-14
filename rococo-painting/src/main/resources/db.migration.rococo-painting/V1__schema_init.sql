-- Создание таблицы painting
CREATE TABLE IF NOT EXISTS painting
(
    id          UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    artist_id   UUID NOT NULL,
    museum_id   UUID,
    content     BYTEA,
    PRIMARY KEY (id),
    CONSTRAINT fk_artist_id FOREIGN KEY (artist_id) REFERENCES artist (id),
    CONSTRAINT fk_museum_id FOREIGN KEY (museum_id) REFERENCES museum (id)
);