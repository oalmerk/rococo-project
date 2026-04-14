create extension if not exists "uuid-ossp";
DROP TABLE IF EXISTS artist CASCADE;
CREATE TABLE artist (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR(255) UNIQUE NOT NULL,
                        biography VARCHAR(2000) NOT NULL,
                        photo BYTEA
);