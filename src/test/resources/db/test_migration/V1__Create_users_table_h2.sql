-- Таблица users
CREATE TABLE users (
                       id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                       login VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

-- Таблица locations
CREATE TABLE locations (
                           id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                           name VARCHAR(255),
                           latitude DOUBLE PRECISION,
                           longitude DOUBLE PRECISION,
                           user_id BIGINT,
                           CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Таблица sessions
CREATE TABLE sessions (
                          id UUID DEFAULT random_uuid() PRIMARY KEY, -- Поменяли порядок
                          expire_at TIMESTAMP NOT NULL,
                          user_id BIGINT,
                          CONSTRAINT fk_user_session FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);