create extension if not exists "uuid-ossp";
CREATE TABLE IF NOT EXISTS users
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username  VARCHAR(50) UNIQUE NOT NULL,
    firstname VARCHAR(255),
    lastname  VARCHAR(255),
    avatar    BYTEA
);